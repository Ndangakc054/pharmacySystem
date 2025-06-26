package com.pharmacy.pharmacySystem.purchases;

import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.drugs.DrugsRepository;
import com.pharmacy.pharmacySystem.sales.SalesNotFoundException;
import com.pharmacy.pharmacySystem.suppliers.Suppliers;
import com.pharmacy.pharmacySystem.suppliers.SuppliersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class PurchasesController {
    @Autowired private PurchasesService service;
    @Autowired private DrugsRepository drugsRepo;
    @Autowired private SuppliersRepository suppliersRepo;

    @GetMapping("/purchases")
    public String showPurchasesList(Model model) {
        List<Purchases> listPurchases = service.listAll();
        model.addAttribute("listPurchases",listPurchases);
        return "purchases";
    }

    @GetMapping("/purchases_user")
    public String showPurchasesListUser(Model model) {
        List<Purchases> listPurchases = service.listAll();
        model.addAttribute("listPurchases",listPurchases);
        return "purchases_user";
    }
    @GetMapping("/addPurchase")
    public String showAddPurchaseForm(Model model) {
        Purchases purchase = new Purchases();
        purchase.setDrug(new Drugs());
        purchase.setSupplier(new Suppliers());
        model.addAttribute("purchase", new Purchases());
        model.addAttribute("listDrugs", drugsRepo.findAllOrderByDrugNameAsc());
        model.addAttribute("listSuppliers", suppliersRepo.findAllOrderBySupplierNameAsc());
        return "addPurchase";
    }
    @GetMapping("/addPurchase_user")
    public String showAddPurchaseFormUser(Model model) {
        Purchases purchase = new Purchases();
        purchase.setDrug(new Drugs());
        purchase.setSupplier(new Suppliers());
        model.addAttribute("purchase", new Purchases());
        model.addAttribute("listDrugs", drugsRepo.findAllOrderByDrugNameAsc());
        model.addAttribute("listSuppliers", suppliersRepo.findAllOrderBySupplierNameAsc());
        return "addPurchase_user";
    }
    @PostMapping("/addPurchase")
    public String savePurchase(@ModelAttribute("purchase") @Validated Purchases purchase,
                               @RequestParam("drugId") Integer drugId, @RequestParam("supplierId") Integer supplierId,
                               BindingResult result, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message","Champs invalides ou vides. Vérifiez les données saisies.");
            //return "redirect:/addPurchase";
            return isAdmin(authentication)
                    ? "redirect:/addPurchase"
                    : "redirect:/addPurchase_user";
        }
        try {
            Drugs drug = drugsRepo.findById(drugId)
                            .orElseThrow(()-> new RuntimeException("Médicament non trouvé"));
            //Liez les médicaments aux achats
            purchase.setDrug(drug);
            //Garder le nom dans la colonne (optionnel)
            purchase.setNameDrug(drug.getNameDrug());
            Suppliers supplier = suppliersRepo.findById(supplierId)
                            .orElseThrow(()-> new RuntimeException("Fournisseur non trouvé"));
            purchase.setSupplier(supplier);
            purchase.setNameSupplier(supplier.getNomFournisseur());
            service.save(purchase);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "L'ajout de l'achat a réussi !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message","L'ajout de l'achat a echoué !");
        }
        return isAdmin(authentication) ? "redirect:/purchases" : "redirect:/purchases_user";
    }


    @GetMapping("purchases/edit/{idPurchase}")
    public String showEditPurchaseForm(@PathVariable("idPurchase") Integer idPurchase,Model model, RedirectAttributes redirectAttributes) {
        //Formater les dates pour le format HTML
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            Purchases purchase = service.get(idPurchase);
            if (purchase.getDateAchat() != null) {
                purchase.setDateAchatFormatted(purchase.getDateAchat().format(formatter));
            } else {
                purchase.setDateAchatFormatted("");
            }
            if (purchase.getDateExpiration() != null) {
                purchase.setDateExpirationFormatted(purchase.getDateExpiration().format(formatter));
            } else {
                purchase.setDateExpirationFormatted("");
            }
            model.addAttribute("purchase", purchase);
            model.addAttribute("listDrugs", drugsRepo.findAllOrderByDrugNameAsc());
            model.addAttribute("listSuppliers", suppliersRepo.findAllOrderBySupplierNameAsc());
            return "editPurchase";
        } catch (PurchasesNotFounfException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "L'achat n'existe pas !");
            return "redirect:/purchases";
        }
    }
    @PostMapping("/purchases/update")
    public String updatePurchase(@ModelAttribute("purchase") @Validated Purchases purchase,
                                 @RequestParam("drugId") Integer drugId,@RequestParam("supplierId") Integer supplierId,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error ->
                    System.err.println("Validation Error; " +error.toString()));
            //System.out.println("Erreur de validation :" +result.getAllErrors());
            return "editPurchase";
        }
        try {
            //Vérification des ID
            /**if(drugId == null || supplierId == null) {
                throw new RuntimeException("ID médicament ou fournisseur manquant");
            }
            // Avant de chercher drug/supplier
            if(drugId == null) {
                throw new IllegalArgumentException("ID médicament non fourni");
            }
            if(supplierId == null) {
                throw new IllegalArgumentException("ID fournisseur non fourni");
            }**/
            Drugs drug = drugsRepo.findById(drugId)
                    .orElseThrow(()-> new RuntimeException("Médicament non trouvé"));
            Suppliers supplier = suppliersRepo.findById(supplierId)
                    .orElseThrow(()-> new RuntimeException("Fournisseur non trouvé"));

            purchase.setNameDrug(drug.getNameDrug());
            purchase.setNameSupplier(supplier.getNomFournisseur());
            // Parse the date from the formatted string
            if (!purchase.getDateAchatFormatted().isEmpty()) {
                LocalDate dateAchat = LocalDate.parse(purchase.getDateAchatFormatted(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                purchase.setDateAchat(dateAchat);
            }
            if (!purchase.getDateExpirationFormatted().isEmpty()) {
                LocalDate dateExpiration = LocalDate.parse(purchase.getDateExpirationFormatted(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                purchase.setDateExpiration(dateExpiration);
            }
            System.out.println("Achat réçu :" +purchase.getIdPurchase());
            System.out.println("Données réçues :"+purchase);
            purchase.setDrug(drug);
            purchase.setSupplier(supplier);
            System.out.println("Achat à sauvegerder: " + purchase);

            service.save(purchase);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "La modification réussie");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Erreur: " +e.getMessage());
        }
        return "redirect:/purchases";
    }
    @GetMapping("purchases/delete/{Id}")
    public String deletePurchase(@PathVariable("Id") Integer Id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(Id);
            redirectAttributes.addFlashAttribute("status","success");
            redirectAttributes.addFlashAttribute("message","L'achat a été supprimé avec succès !");
        } catch ( PurchasesNotFounfException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "L'achat n'existe pas !");
        }
        return "redirect:/purchases";
    }
    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
