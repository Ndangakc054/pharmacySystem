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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
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
    /**@GetMapping("/sales")
    public ResponseEntity<List<Sales>> getSalesByDate(@RequestParam String dateVente) {
        try {
            LocalDate localDate = LocalDate.parse(dateVente);
            List<Sales> sales = service.listSalesByDate(localDate);
            return ResponseEntity.ok(sales);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }**/

    @GetMapping("/sales_user")
    public String showSalesPageUser(Model model) {
        List<Stock> listStock = stockService.listAll();
        model.addAttribute("listStock", listStock);
        return "sales_user";
    }


    @GetMapping("/recentSales")
    public String showRecentSalesPage(
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        // 1. Si la date est nulle (premier chargement), on prend aujourd'hui
        if (date == null) {
            date = LocalDate.now();
        }
        // 2. Récupérer le total des ventes pour cette date spécifique
        BigDecimal salesTotal = service.getSalesTotalByDate(date);
        // Gérer le cas où c'est null (pas de ventes ce jour-là)
        if (salesTotal == null) {
            salesTotal = BigDecimal.ZERO;
        }
        String formattedSalesTotal = salesTotal.setScale(2,RoundingMode.HALF_UP).toPlainString();
        // 3. Récupérer la liste filtrée par cette date
        List<Sales> listSales = service.getSalesByDate(date);
        // 4. Envoyer les données à la vue
        model.addAttribute("listSales", listSales);
        model.addAttribute("todaySalesTotal", formattedSalesTotal);
        model.addAttribute("selectedDate", date); // Important pour l'input date
        return "recentSales";
    }

    @GetMapping("/recentSales_user")
    public String showRecentSalesUserPage(
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        // 1. Si la date est nulle (premier chargement), on prend aujourd'hui
        if (date == null) {
            date = LocalDate.now();
        }
        // 2. Récupérer le total des ventes pour cette date spécifique
        BigDecimal salesTotal = service.getSalesTotalByDate(date);
        // Gérer le cas où c'est null (pas de ventes ce jour-là)
        if (salesTotal == null) {
            salesTotal = BigDecimal.ZERO;
        }
        String formattedSalesTotal = salesTotal.setScale(2,RoundingMode.HALF_UP).toPlainString();
        // 3. Récupérer la liste filtrée par cette date
        List<Sales> listSales = service.getSalesByDate(date);
        // 4. Envoyer les données à la vue
        model.addAttribute("listSales", listSales);
        model.addAttribute("todaySalesTotal", formattedSalesTotal);
        model.addAttribute("selectedDate", date); // Important pour l'input date
        return "recentSales_user";
    }

   @GetMapping("/sales/edit/{Id}")
    public String showEditSalesForm(@PathVariable("Id") Integer idSale, Model model, RedirectAttributes redirectAttributes) {
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            Sales sale = service.get(idSale);
            model.addAttribute("sale", sale);
            return "editSales";
        } catch (SalesNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "La vente n'existe pas");
            return "editSales";
        }
    }

    @PostMapping("sales/update")
    public String updateSale(
            @RequestParam("idSale") Integer idSale,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("prixUnitaire") BigDecimal prixUnitaire,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // 1. Récupérer la vente existante
            Sales existingSale = service.get(idSale);

            // 2. Mettre à jour uniquement les champs autorisés
            existingSale.setQuantity(quantity);
            existingSale.setPrixUnitaire(prixUnitaire);

            // 3. Calculer automatiquement le nouveau total
            BigDecimal total = prixUnitaire.multiply(BigDecimal.valueOf(quantity));
            existingSale.setPrixTotal(total);

            // 4. Sauvegarder
            service.update(existingSale);

            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "Modification effectuée avec succès");
        } catch (SalesNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Erreur lors de la modification");
        }
        return "redirect:/recentSales";
    }

    /**@PostMapping("sales/update")
    public String updateSale(@ModelAttribute("salesForm") @Validated SalesForm salesForm, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.out.println("Erreur de validation : "+result.getAllErrors());
            return "editSales";
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

    /**@PostMapping("/sales/update/{invoiceId}")
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
    }**/

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

    @GetMapping("/allSales")
    public String showAllSalesPage(Model model){
        List<Sales> listSales = service.listAll();
        model.addAttribute("listSales", listSales);
        return "allSales";

    }

    @GetMapping("/allSales_user")
    public String showAllSalesUserPage(Model model){
        List<Sales> listSales = service.listAll();
        model.addAttribute("listSales", listSales);
        return "allSales_user";

    }

    @GetMapping("/sales/export/pdf")
    public void exportSalesToPdf(@RequestParam(value = "date", required = false) String dateStr,
                                 HttpServletResponse response) throws IOException {

        // 1. Récupérer les ventes filtrées (même logique que pour ton tableau HTML)
        LocalDate date = (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : LocalDate.now();
        List<Sales> filteredSales = service.findByDate(date); // Ta méthode de service existante

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=rapport_ventes_" + date + ".pdf");

        // 2. Passer la liste ET la date sélectionnée à l'exportateur
        SalesPdfExporter exporter = new SalesPdfExporter(filteredSales, date);
        exporter.export(response);
    }

    /**@GetMapping("/sales/export/pdf")
    public void exportSalesToPdf(HttpServletResponse response) throws IOException {
        // Récupérer la liste complète des ventes (ou filtrée selon tes besoins)
        List<Sales> listSales = service.listAll(); // Adapte selon ton Service

        // Configuration pour afficher le PDF dans le navigateur (inline) au lieu de le télécharger direct
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=rapport_ventes.pdf");

        SalesPdfExporter exporter = new SalesPdfExporter(listSales);
        exporter.export(response);
    }**/

}

