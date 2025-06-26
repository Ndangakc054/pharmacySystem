package com.pharmacy.pharmacySystem.invoices;

import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.drugs.DrugsService;
import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import com.pharmacy.pharmacySystem.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class InvoiceController {
    @Autowired private InvoiceService service;
    @Autowired private UsersService usersService;
    @Autowired private DrugsService drugsService;
    @Autowired private UsersRepository usersRepo;
    @Autowired private InvoicesRepository invoicesRepo;

    @GetMapping("/invoicesPage")
    public String showInvoicesPage(Model model) {
        List<Invoices> listInvoices = service.listAll();
        model.addAttribute("listInvoices", listInvoices);
        return "invoicesPage";
    }
    @GetMapping("/invoices/edit/{id}")
    public String ShoweditForm(@PathVariable Integer id, Model model) {
        Invoices invoice = service.getInvoiceById(id);
        List<Users> listUsers = usersService.listAll();
        List<Drugs> listDrugs = drugsService.listAll();

        model.addAttribute("invoice", invoice);
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("listDrugs", listDrugs);
        return "editInvoices";
    }
    @PostMapping("/invoices/update/{id}")
    public String updateInvoice(@PathVariable("id") Integer id, @ModelAttribute Invoices invoice, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "editInvoices";
        }
        try {
            // Vérifier que l'utilisateur est bien associé
            if (invoice.getUser() == null || invoice.getUser().getId() == null) {
                throw new RuntimeException("L'utilisateur est manquant");
            }
            // Récupérer l'utilisateur depuis la base de données
            Users user = usersRepo.findById(invoice.getUser().getId())
                            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            invoice.setUser(user);
            service.updateInvoice(id, invoice);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "Facture mise à jour avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Erreur lors de la mise à jour de la facture : " + e.getMessage());
        }
        return "redirect:/invoicesPage";
    }
    @GetMapping
    public ResponseEntity<byte[]> getPdfFile(@PathVariable Integer id) {
        Invoices invoice = invoicesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture introuvable"));
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "inline; filename=facture.pdf")
                .body(invoice.getPdfInvoice());
    }

    @GetMapping("invoices/delete/{idInvoice}")
    public String deleteInvoice(@PathVariable("idInvoice") Integer Id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteInvoice(Id);
            redirectAttributes.addFlashAttribute("status","success");
            redirectAttributes.addFlashAttribute("message","La facture a été supprimée avec succès !");
        } catch (InvoicesNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "La facture n'existe pas !");
        }
        return "redirect:/invoicesPage";
    }

    /**@GetMapping("/invoicesPage/{id}/pdf")
    public ResponseEntity<byte[]> downnloadInvoicePdf(@PathVariable Integer idInvoice) {
        Invoices invoice = repo.findById(idInvoice)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facture_" + idInvoice + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(invoice.getPdfInvoice());
    }**/
}
