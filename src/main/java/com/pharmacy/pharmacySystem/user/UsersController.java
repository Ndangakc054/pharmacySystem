package com.pharmacy.pharmacySystem.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {
    @Autowired private UsersService service;

    @GetMapping("/users")
    public String showUserList(Model model, Principal principal) {
        List<Users> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);

        //Récupérer l'utilisateur connecté 
        String username = principal.getName();
        Optional<Users> loggedUser = service.findByUsername(username);
        loggedUser.ifPresent(user -> model.addAttribute("loggedUser", user));
        /**if (loggedUser.isPresent()) {
            Users user = loggedUser.get();
        }**/
        return  "users";
    }
    @GetMapping("/addUser")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new Users());
        return "addUser";
    }
    @PostMapping("/addUser")
    public String saveUSer(@ModelAttribute("user") @Validated Users user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Champs invalides ou vides. Vérifiez les données saisies");
            return  "redirect:/addUser";
        }
        try {
            service.save(user);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "L'ajout de l'utilisateur a réussi !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message","L'ajout de l'utilisateur a échoué !");
        }
        return "redirect:/users";
    }
    @GetMapping("users/edit/{Id}")
    public String showEditUSerForm(@PathVariable("Id") Integer Id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Users user = service.get(Id);
            model.addAttribute("user", user);
            return  "editUser";
        } catch (UsersNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "L'utilisateur n'existe pas");
            return "editUser";
        }
    }

    /**@GetMapping("users/edit/{Id}")
    public String showEditProfileForm(@PathVariable("Id") Integer Id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Users user = service.get(Id);
            model.addAttribute("user", user);
            return  "editProfile";
        } catch (UsersNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "L'utilisateur n'existe pas");
            return "editProfile";
        }
    }**/

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") @Validated Users user, BindingResult result, RedirectAttributes redirectAttributes,
                             Principal principal, Model model) {
        if (result.hasErrors()) {
            System.out.println("Erreur de validation : "+result.getAllErrors());

            return "editUser";
        }
        try {
            System.out.print("User Id réçu :" + user.getId());
            System.out.print("Données reçues : " +user);
            service.save(user);

            // Mettre à jour l'utilisateur connecté dans le modèle
            String username = principal.getName();
            Optional<Users> updatedUSer = service.findByUsername(username);
            if (updatedUSer.isPresent()) {
                model.addAttribute("loggedUser", updatedUSer.get());
            }
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "La modification de l'utilisateur est effectuée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Echec  de la modification");
        }

        return "redirect:/users";
    }

    @GetMapping("/profile/edit/{id}")
    public String showProfileForm(@PathVariable("id") Integer id, Model model) {
        try {
            // On récupère l'utilisateur par l'ID passé dans le lien
            Users user = service.get(id);

            // On l'ajoute au modèle sous le nom "user" pour que th:object l'utilise
            model.addAttribute("user", user);

            return "editProfile"; // Affiche ta page editProfile.html
        } catch (UsersNotFoundException e) {
            return "redirect:/home_user";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") Users user, RedirectAttributes ra) {
        try {
            // On sauvegarde les modifications (Nom, Mot de passe)
            service.save(user);
            ra.addFlashAttribute("status", "success");
            ra.addFlashAttribute("message", "Votre profil a été mis à jour !");
            return "redirect:/home_user";
        } catch (Exception e) {
            ra.addFlashAttribute("status", "error");
            ra.addFlashAttribute("message", "Erreur lors de la mise à jour");
            return "redirect:/profile/edit/" + user.getId();
        }
        //return "redirect:/home_user";
    }

    /**@GetMapping("/profile/edit/{idUser}")
    public String showProfileForm(@PathVariable("id") Integer id, Principal principal, Model model) {
        String username = principal.getName();
        Users user = service.findByUsername(username).orElseThrow();
        model.addAttribute("user", user);
        return "editProfile"; // Une page dédiée ou une section
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") Users user, Principal principal, RedirectAttributes ra) {
        try {
            // Sécurité : on s'assure que l'utilisateur ne modifie que son propre compte
            service.save(user);
            ra.addFlashAttribute("status", "success");
            ra.addFlashAttribute("message", "Profil mis à jour !");
        } catch (Exception e) {
            ra.addFlashAttribute("status", "error");
            ra.addFlashAttribute("message", "Erreur lors de la mise à jour");
        }
        return "redirect:/home_user"; // Redirige vers l'accueil
    }**/

    @GetMapping("users/delete/{Id}")
    public String deleteUser(@PathVariable("Id") Integer Id, RedirectAttributes redirectAttributes){
        try {
            service.delete(Id);
            redirectAttributes.addFlashAttribute("status","success");
            redirectAttributes.addFlashAttribute("message","Suppression de l'utilisateur effectuée !");
        } catch (UsersNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "L'utilisateur n'existe pas !");
        }
        return "redirect:/users";
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
