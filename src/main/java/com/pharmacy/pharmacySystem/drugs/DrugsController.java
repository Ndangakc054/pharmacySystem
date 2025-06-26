package com.pharmacy.pharmacySystem.drugs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class DrugsController {
    @Autowired private DrugsService service;

    @GetMapping("/drugs")
    public String showDrugList(Model model) {
        List<Drugs> listDrugs = service.listAll();
        model.addAttribute("listDrugs", listDrugs);

        return "drugs";
    }

    @GetMapping("/drugs_user")
    public String showDrugListUser(Model model) {
        List<Drugs> listDrugs = service.listAll();
        model.addAttribute("listDrugs", listDrugs);

        return "drugs_user";
    }

    @GetMapping("/addDrug")
    public String showAddDrugForm(Model model) {
        model.addAttribute("drug", new Drugs());
        return "addDrug";
    }
    @GetMapping("/addDrug_user")
    public String showAddDrugFormUser(Model model) {
        model.addAttribute("drug", new Drugs());
        return "addDrug_user";
    }
    @PostMapping("/addDrug")
    public String saveDrug(@ModelAttribute("drug") @Validated Drugs drug, BindingResult result, RedirectAttributes redirectAttributes, Authentication authentication) {
        if(result.hasErrors()) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Champs invalides ou vides. Vérifiez les données saisies.");
            //return "redirect:/addDrug";
            // Redirection vers le bon formulaire en cas d'erreur
            return isAdmin(authentication)
                    ? "redirect:/addDrug"
                    : "redirect:/addDrug_user";
        }
        try {
            service.save(drug);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "L'ajout du médicament a réussi !");
        } catch (DrugsNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message","L'ajout du médicament a echoué !");
        }
        return isAdmin(authentication) ? "redirect:/drugs" : "redirect:/drugs_user";
    }


    @GetMapping("drugs/edit/{Id}")
    public String ShowEditDrugForm(@PathVariable("Id") Integer idDrug, Model model, RedirectAttributes redirectAttributes){
        //Formater le champs date pour le format HTMl
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            Drugs drug = service.get(idDrug);
            if (drug.getDateExpiration() != null) {
                drug.setDateExpirationFormatted(drug.getDateExpiration().format(formatter));
            } else {
                drug.setDateExpirationFormatted("");
            }
            model.addAttribute("drug", drug);
            return "editDrug";
        } catch (DrugsNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Le médicaments n'existe pas !");
            return "redirect:/drugs";
        }
    }
    @PostMapping("/drugs/update")
    public String updateDrug(@ModelAttribute("drug") @Validated Drugs drug, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.out.println("Erreur de validation : " + result.getAllErrors());
            return "editDrug";
        }
        try {
            // Parse the date from the formatted string
            if(!drug.getDateExpirationFormatted().isEmpty()) {
                LocalDate dateExpiration = LocalDate.parse(drug.getDateExpirationFormatted(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                drug.setDateExpiration(dateExpiration);
            }
            System.out.print("Drug Id réçu :" + drug.getNameDrug());
            System.out.print("Données reçues : " +drug);
            service.save(drug);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "La modification du médicament est effectué avec succès");
        } catch (DrugsNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "La modification à échoué");
        }
        return "redirect:/drugs";
    }
    @GetMapping("drugs/delete/{Id}")
    public String deleteDrug(@PathVariable("Id") Integer idDrug, RedirectAttributes redirectAttributes){
        try {
            service.delete(idDrug);
            redirectAttributes.addFlashAttribute("status","success");
            redirectAttributes.addFlashAttribute("message","Le médicament a été supprimer avec succès !");
        } catch (DrugsNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Le médicaments n'existe pas !");
        }
        return "redirect:/drugs";
    }
    private boolean isAdmin(Authentication authentication) {
        if(authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
