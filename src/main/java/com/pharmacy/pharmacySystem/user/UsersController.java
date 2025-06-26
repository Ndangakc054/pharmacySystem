package com.pharmacy.pharmacySystem.user;

import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") @Validated Users user, BindingResult result, RedirectAttributes redirectAttributes, Principal principal, Model model) {
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
            redirectAttributes.addFlashAttribute("message", "La modification de l'utilisateur est effectué avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Echec  de la modification");
        }
        return "redirect:/users";
    }
    @GetMapping("users/delete/{Id}")
    public String deleteUser(@PathVariable("Id") Integer Id, RedirectAttributes redirectAttributes){
        try {
            service.delete(Id);
            redirectAttributes.addFlashAttribute("status","success");
            redirectAttributes.addFlashAttribute("message","L'utilisateur a été supprimer avec succès !");
        } catch (UsersNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "L'utilisateur n'existe pas !");
        }
        return "redirect:/users";
    }
}
