package com.pharmacy.pharmacySystem.orders;

import com.itextpdf.commons.utils.Base64;
import com.itextpdf.io.exceptions.IOException;
import com.pharmacy.pharmacySystem.purchaseFiles.PurchaseFiles;
import com.pharmacy.pharmacySystem.purchaseFiles.PurchaseFilesRepository;
import com.pharmacy.pharmacySystem.stock.Stock;
import com.pharmacy.pharmacySystem.stock.StockService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Controller
public class OrdersController {
    @Autowired private OrdersService service;
    @Autowired private StockService stockService;
    @Autowired private PurchaseFilesRepository filesRepo;



    @GetMapping("/orders")
    public String showStockList(Model model) {
        List<Stock> listStock = service.refreshOrders();
        model.addAttribute("listStock", listStock);
        return "orders";
    }

    @GetMapping("/orders_user")
    public String showStockListUser(Model model) {
        List<Stock> listStock = service.refreshOrders();
        model.addAttribute("listStock", listStock);
        return "orders_user";
    }
    /**@PostMapping("/validate")
    public ResponseEntity<String> validateOrder(@RequestBody List<OrdersDTO> ordersDTOList) {
        service.saveOrders(ordersDTOList);
        return ResponseEntity.ok("Commandes enregistrées et PDF généré avec succès");
    }**/

    @PostMapping("/refreshOrders")
    @ResponseBody
    public List<Stock> refreshOrders() {
        return service.refreshOrders();
    }

    @PostMapping("/orders/save")
    public ResponseEntity<?> saveOrders(@RequestBody OrdersForm ordersForm) {
        try {
            PurchaseFiles file = service.saveOrders(ordersForm);
            // Retourner l'ID du fichier ou une URL pour le PDF
            return ResponseEntity.ok().body(Map.of(
                    "message", "Fiche validée avec succès !",
                    "pdfUrl", "/orders/pdf/" + file.getIdFile()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur : " + e.getMessage());
        }
    }
    @GetMapping("/orders/pdf/{id}")
    public ResponseEntity<byte[]> getPdf(@PathVariable Integer id) {
        PurchaseFiles file = filesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche de réquisition introuvable"));
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "inline; filename=commande.pdf")
                .body(file.getPdfOrders());
    }
    /**@GetMapping("/download-pdf/{fileId}")
    public void downloadPdf(@PathVariable BigInteger fileId, HttpServletResponse response) throws IOException {
        PurchaseFiles file = filesRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé"));

        //// Vérifiez que le PDF n'est pas null
        if (file.getPdfOrders() == null) {
            throw new RuntimeException("Le fichier PDF est vide ou non généré.");
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=commande.pdf");

        // Écrire le PDF dans le flux de réponse
        try (OutputStream ops = response.getOutputStream()){
            ops.write(file.getPdfOrders());
            ops.flush();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Erreur lors de l'écriture du PDF dans la réponse", e);
        }
    }**/
}
