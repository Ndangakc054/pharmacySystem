package com.pharmacy.pharmacySystem.sales;

import com.pharmacy.pharmacySystem.invoices.InvoiceService;
import com.pharmacy.pharmacySystem.invoices.Invoices;
import com.pharmacy.pharmacySystem.invoices.InvoicesRepository;
import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.drugs.DrugsRepository;
import com.pharmacy.pharmacySystem.drugs.DrugsService;
import com.pharmacy.pharmacySystem.stock.Stock;
import com.pharmacy.pharmacySystem.stock.StockService;
import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import com.pharmacy.pharmacySystem.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SalesController {
    @Autowired private SalesService service;
    @Autowired private StockService stockService;
    @Autowired private UsersService usersService;
    @Autowired private DrugsService drugsService;
    @Autowired private InvoiceService invoiceService;
    @Autowired private InvoicesRepository invoicesRepo;
    @Autowired private UsersRepository usersRepo;
    @Autowired private DrugsRepository drugsRepo;
    @Autowired private SalesRepository repo;


    @GetMapping("/sales")
    public String showSalesPage(Model model) {
        List<Stock> listStock = stockService.listAll();
        model.addAttribute("listStock", listStock);
        return "sales";
    }

    @GetMapping("/sales_user")
    public String showSalesPageUser(Model model) {
        List<Stock> listStock = stockService.listAll();
        model.addAttribute("listStock", listStock);
        return "sales_user";
    }

    @GetMapping("/recentSales")
    public String showRecentSalesPage(Model model) {
        List<Sales> listSales = service.listAll();
        model.addAttribute("listSales", listSales);
        return "recentSales";
    }

    @GetMapping("/recentSales_user")
    public String showRecentSalesPageUser(Model model) {
        List<Sales> listSales = service.listAll();
        model.addAttribute("listSales", listSales);
        return "recentSales_user";
    }
   /**@GetMapping("/sales/edit/{Id}")
    public String showEditSalesForm(@PathVariable("Id") Integer Id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Sales sale = service.get(Id);
            model.addAttribute("sale", sale);
            return "editRecentSales";
        } catch (SalesNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "La vente n'existe pas");
            return "editRecentSales";
        }
    }**/

    /**@PostMapping("sales/update")
    public String updateSale(@ModelAttribute("salesForm") @Validated SalesForm salesForm, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.out.println("Erreur de validation : "+result.getAllErrors());
            return "editRecentSales";
        }
        try {
            System.out.println("Vente réçue :" +salesForm.getItems());
            System.out.println("Données réçues :" +salesForm);
            service.saveSales(salesForm);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "La modification de la vente est effectué avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "La modification a échoué");
        }
        return "redirect:/recentSales";
    }**/

    @PostMapping("/sales/update/{invoiceId}")
    public ResponseEntity<?> updateInvoice(@PathVariable Integer invoiceId, @RequestBody SalesForm updateSalesForm) {
        try {
            Users user = usersRepo.findById(updateSalesForm.getUserId()).orElseThrow();
            List<Sales> newSalesList = new ArrayList<>();
            for (SalesItem item : updateSalesForm.getItems()) {
                Drugs drug = drugsRepo.findById(item.getIdDrug()).orElseThrow();
                Sales sale = new Sales();
                sale.setDrug(drug);
                sale.setQuantity(item.getQuantity());
                sale.setPrixUnitaire(item.getUnitPrice());
                sale.setPrixTotal(item.getTotalAmount());
                sale.setUser(user);
                sale.setDateVente(LocalDateTime.now());
                sale.setInvoice(invoicesRepo.findById(invoiceId).orElseThrow());
                newSalesList.add(sale);
            }
            repo.saveAll(newSalesList);
            // Régénérer le PDF
            byte[] newPdf = service.generateInvoicePdf(updateSalesForm, user);
            Invoices invoice = invoicesRepo.findById(invoiceId).orElseThrow();
            invoice.setPdfInvoice(newPdf);
            invoicesRepo.save(invoice);

            return ResponseEntity.ok().body("Facture mise à jour !");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur de la mise à jour de la facture : " +e.getMessage());
        }
    }

    @PostMapping("/sales/save")
    public ResponseEntity<?> saveSales(@RequestBody SalesForm salesForm) {
        try{
            Invoices invoice = service.saveSales(salesForm);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Facture validée",
                    "pdfUrl", "/sales/pdf/" + invoice.getIdInvoice()
            ));
        } catch (Exception e) {
            //e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur : " + e.getMessage());
        }
    }

    @GetMapping("/sales/pdf/{id}")
    public ResponseEntity<byte[]> getPdf(@PathVariable Integer id) {
        Invoices invoice = invoicesRepo.findById(id)
                .orElseThrow(() ->new RuntimeException("Facture introuvable"));

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "inline; filename=facture.pdf")
                .body(invoice.getPdfInvoice());
    }
    @GetMapping("sales/delete/{idSale}")
    public String deleteSale(@PathVariable("idSale") Integer idSale, RedirectAttributes redirectAttributes) {
        try {
            service.deleteSale(idSale);
            redirectAttributes.addFlashAttribute("status","success");
            redirectAttributes.addFlashAttribute("message","La vente a été supprimée avec succès !");
        } catch ( SalesNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "La vente n'existe pas !");
        }
        return "redirect:/recentSales";
    }

}

