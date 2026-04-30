package com.pharmacy.pharmacySystem.suppliers;

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

import java.util.List;

@Controller
public class SuppliersController {
    @Autowired private  SuppliersService service;


    @GetMapping("/suppliers")
    public String showSupplierList(Model model) {
        List<Suppliers> listSuppliers = service.listAll();
        model.addAttribute("listSuppliers", listSuppliers);
        return  "suppliers";
    }

    @GetMapping("/suppliers_user")
    public String showSupplierListUser(Model model) {
        List<Suppliers> listSuppliers = service.listAll();
        model.addAttribute("listSuppliers", listSuppliers);
        return  "suppliers_user";
    }
    @GetMapping("/addSupplier")
    public String showAddSupplierForm(Model model) {
        model.addAttribute("supplier", new Suppliers());
        return "addSupplier";
    }
    @GetMapping("/addSupplier_user")
    public String showAddSupplierFormUser(Model model) {
        model.addAttribute("supplier", new Suppliers());
        return "addSupplier_user";
    }
    @PostMapping("/addSupplier")
    public String saveSupplier(@ModelAttribute("supplier") @Validated Suppliers supplier, BindingResult result, RedirectAttributes redirectAttributes, Authentication authentication){
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("status","error");
            redirectAttributes.addFlashAttribute("message","Champs invalides ou vides. Vérifiez les données saisies");
            //return "redirect:/addSupplier";
            // Redirection vers le bon formulaire en cas d'erreur
            return isAdmin(authentication)
                    ? "redirect:/addDrugStock"
                    : "redirect:/addDrugStock_user";
        }
        try{
            service.save(supplier);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "L'ajout du fournisseur a réussi !");
        }catch (SuppliersNotFoundException e){
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "L'ajout du Fournisseur a échoué !");
        }
        return isAdmin(authentication) ? "redirect:/suppliers" : "redirect:/suppliers_user";
    }


    @GetMapping("suppliers/edit/{Id}")
    public String showEditSupplierForm(@PathVariable("Id") Integer idSupplier, Model model, RedirectAttributes redirectAttributes) {
        try{
            Suppliers supplier = service.get(idSupplier);
            model.addAttribute("supplier", supplier);
            return  "editSupplier";
        }catch (SuppliersNotFoundException e){
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Le fournisseur n'existe pas");
            return "redirect:/suppliers";
        }
    }
    @PostMapping("/suppliers/update")
    public String updateSupplier(@ModelAttribute("supplier") @Validated Suppliers supplier, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.out.println("Erreur de validation :" +result.getAllErrors());
            return "editSupplier";
        }
        try{
            System.out.println("Supplier Id réçu :"+supplier.getNomFournisseur());
            System.out.println("Données reçues :"+supplier);
            service.save(supplier);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "La modification du fournisseur est effectué avec succès");
        } catch (SuppliersNotFoundException e){
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "La modification a échoué");
        }
        return "redirect:/suppliers";
    }
    @GetMapping("suppliers/delete/{Id}")
    public String deleteSupplier(@PathVariable("Id") Integer idSupplier, RedirectAttributes redirectAttributes){
        try{
            service.delete(idSupplier);
            redirectAttributes.addFlashAttribute("status","success");
            redirectAttributes.addFlashAttribute("message","Fournisseur supprimé(e)");
        }catch (SuppliersNotFoundException e) {
            redirectAttributes.addFlashAttribute("status","error");
            redirectAttributes.addFlashAttribute("message","Une erreur est survenue lors de la suppression.");
        }
        return "redirect:/suppliers";
    }
    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
