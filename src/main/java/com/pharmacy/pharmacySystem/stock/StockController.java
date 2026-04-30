package com.pharmacy.pharmacySystem.stock;

import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.drugs.DrugsRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
public class StockController {
    @Autowired private StockService service;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private DrugsRepository drugsRepo;
    @Autowired private StockRepository repo;
    @Autowired private StockExcelImportService importService;


    @GetMapping("/stock")
    public String showStockList(Model model) {

        //Récupérer le total de l'inventaire
        BigDecimal totalInventory = service.getTotalStockValue();
        // Formater le montant
        String formattedInventoryTotal = totalInventory.setScale(2, RoundingMode.HALF_UP)
                .toPlainString();

        List<Stock> listStock = service.listAll();
        model.addAttribute("listStock", listStock);
        model.addAttribute("totalInventory", formattedInventoryTotal);
        return "stock";
    }

    @GetMapping("/stock_user")
    public String showStockListUser(Model model) {
        //Récupérer le total de l'inventaire
        BigDecimal totalInventory = service.getTotalStockValue();
        // Formater le montant
        String formattedInventoryTotal = totalInventory.setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
        List<Stock> listStock = service.listAll();
        model.addAttribute("listStock", listStock);
        model.addAttribute("totalInventory", formattedInventoryTotal);
        return "stock_user";
    }

    @GetMapping("/addDrugStock")
    public String showAddDrugStockForm(Model model) {
        Stock stock = new Stock();
        stock.setDrug(new Drugs());
        model.addAttribute("stock", new Stock());
        model.addAttribute("listDrugs", drugsRepo.findAllOrderByDrugNameAsc());
        return "addDrugStock";
    }
    @GetMapping("/addDrugStock_user")
    public String showAddDrugStockFormUser(Model model) {
        Stock stock = new Stock();
        stock.setDrug(new Drugs());
        model.addAttribute("stock", new Stock());
        model.addAttribute("listDrugs", drugsRepo.findAllOrderByDrugNameAsc());
        return "addDrugStock_user";
    }
    @PostMapping("/addDrugStock")
    public String saveDrugStock(@ModelAttribute("stock") @Validated Stock stock,
                                @RequestParam("drugId") Integer drugId,BindingResult result,
                                RedirectAttributes redirectAttributes, Authentication authentication) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Champs invalides ou vides. Vérifiez les données saisies.");
            //return "redirect:/addDrugStock";
            // Redirection vers le bon formulaire en cas d'erreur
            return isAdmin(authentication)
                    ? "redirect:/addDrugStock"
                    : "redirect:/addDrugStock_user";
        }
        try {
            Drugs selectedDrug = drugsRepo.findById(drugId)
                            .orElseThrow(()-> new RuntimeException("Médicament non trouvé"));
            //Liez le médicament au stock
            stock.setDrug(selectedDrug);

            //pour garder le nom dans la colonne (optionnel)
            stock.setNameDrug(selectedDrug.getNameDrug());

            // Génération du batch ID pour les nouveaux stocks
            /**if (stock.getBatch() == null || stock.getBatch().isEmpty()) {
                String batchId = "BATCH_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                stock.setBatch(batchId);
            }**/
            service.save(stock);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "L'ajout du médicament dans le stock a réussi !");
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message","L'ajout du médicament dans le stock a echoué !");
        }
        return isAdmin(authentication) ? "redirect:/stock" : "redirect:/stock_user";
    }

    @GetMapping("stock/edit/{idStock}")
    public String showEditStockForm(@PathVariable("idStock") Integer idStock, Model model, RedirectAttributes redirectAttributes) {
        // Formater les champs date pour le format HTML
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            Stock stock = service.get(idStock);

            //Initialiser drug si null

            if (stock.getDateExpiration() != null) {
                stock.setDateExpirationFormatted(stock.getDateExpiration().format(formatter));
            } else {
                stock.setDateExpirationFormatted("");
            }
            if (stock.getDateEntree() != null) {
                stock.setDateEntreeFormatted(stock.getDateEntree().format(formatter));
            }else {
                stock.setDateEntreeFormatted("");
            }
            model.addAttribute("stock", stock);
            model.addAttribute("listDrugs", drugsRepo.findAll());
            return "editDrugStock";
        } catch (StockNotFoundException e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "Le médicaments n'existe pas");
            return "editDrugStock";
        }
    }
    @PostMapping("/stock/update")
    public String updateDrugStock(@ModelAttribute("stock") @Validated Stock stock,
                                  @RequestParam("drugId") Integer drugId,
                                  BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.out.println("Erreur de validation :" +result.getAllErrors());
            return "editDrugStock";
        }
        try {
            Drugs drug = drugsRepo.findById(drugId)
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));
            // Conserver le batch ID existant pour les modifications
            /**Stock existingStock = repo.findById(stock.getIdStock())
                    .orElseThrow(() -> new RuntimeException("Stock non trouvé"));
            stock.setBatch(existingStock.getBatch());**/
            // Parse the date from the formatted string
            if (!stock.getDateExpirationFormatted().isEmpty()) {
                LocalDate dateExpiration = LocalDate.parse(stock.getDateExpirationFormatted(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                stock.setDateExpiration(dateExpiration);
            }
            if (!stock.getDateEntreeFormatted().isEmpty()) {
               LocalDate dateEntree = LocalDate.parse(stock.getDateEntreeFormatted(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
               stock.setDateEntree(dateEntree);
            }

            System.out.println("Stock Id réçu :"+stock.getIdStock());
            System.out.println("Données réçues :"+stock);
            stock.setDrug(drug);
            service.save(stock);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message", "La modification du médicament dans le stock est effectuée avec succès");
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message", "La modification a échoué");
        }
        return "redirect:/stock";
    }
    @GetMapping("stock/delete/{idStock}")
    public String deleteDrugStock(@PathVariable("idStock") Integer idStock, RedirectAttributes redirectAttributes) {
        try {
            service.delete(idStock);
            redirectAttributes.addFlashAttribute("status", "success");
            redirectAttributes.addFlashAttribute("message","Le médicament a été supprimé avec succès dans le stock !");
        } catch (StockNotFoundException e){
            redirectAttributes.addFlashAttribute("status", "error");
            redirectAttributes.addFlashAttribute("message","Le médicament n'existe pas dans le stock");
        }
        return "redirect:/stock";
    }

    @GetMapping("/expiry")
    public String getDrugsByExpiration(Model model) {
        List<StockExpirationDTO> listExpiry = service.getDrugsExpiration();
        model.addAttribute("listExpiry", listExpiry);
        return "expiry";
    }

    @GetMapping("/expiry_user")
    public String getDrugsByExpirationUser(Model model) {
        List<StockExpirationDTO> listExpiry = service.getDrugsExpiration();
        model.addAttribute("listExpiry", listExpiry);
        return "expiry_user";
    }

    @GetMapping("/stock/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");

        // Définir le nom du fichier avec la date actuelle c'est plus pro
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Inventaire_" + LocalDate.now() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        // Récupérer tout le stock depuis la base de données
        List<Stock> listStock = service.listAll(); // Adapte selon le nom de ta méthode find all

        InventoryExcelExporter excelExporter = new InventoryExcelExporter(listStock);

        excelExporter.export(response);
    }

    @PostMapping("/stock/import")
    public String importExcelInventoryFile(@RequestParam("file")MultipartFile file, RedirectAttributes ra){
        if (file.isEmpty()){
            ra.addFlashAttribute("message", "Veuillez sélectionner un fichier !");
            return "redirect:/stock";
        }

        try{
            importService.save(file);
            ra.addFlashAttribute("status", "success");
            ra.addFlashAttribute("message", "Importation réussie !");
        } catch (Exception e) {
            ra.addFlashAttribute("status", "error");
            ra.addFlashAttribute("message", "Erreur: " + e.getMessage());
        }
        return "redirect:/stock";
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
