package com.pharmacy.pharmacySystem.orders;


import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.pharmacy.pharmacySystem.stock.Stock;
import com.pharmacy.pharmacySystem.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static javax.swing.text.StyleConstants.setBold;

@Controller
public class OrdersController {
    @Autowired private OrdersService service;
    @Autowired private StockService stockService;



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

    @PostMapping("/refreshOrders")
    @ResponseBody
    public List<Stock> refreshOrders() {
        return service.refreshOrders();
    }

    @PostMapping("/requisition/generate-pdf")
    public ResponseEntity<byte[]> generateRequisitionPdf(@RequestBody List<Map<String, Object>> items) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Titre


            document.add(new Paragraph("PHARMACIE HUMANOPHAR")
                    .setTextAlignment(TextAlignment.LEFT));
            //INFORMATION SUR LA FACTURE
            document.add(new Paragraph("NIF : 4000020661"))
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(new Paragraph("Téléphone : 79970620"))
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(new Paragraph("Date : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new Paragraph("FICHE DE REQUISITION")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold());
            document.add(new Paragraph(" ")); // Espacement

            // Tableau (2 colonnes : Médicament et Quantité)
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1})).useAllAvailableWidth();
            table.addHeaderCell(new Cell().add(new Paragraph("Médicament").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantité").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Prix Unitaire").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Prix Total").setBold()));

            // Remplissage des données reçues du JS
            for (Map<String, Object> item : items) {
                table.addCell(item.get("drugName").toString());
                table.addCell(item.get("quantity").toString());
                table.addCell("");
                table.addCell("");
            }

            document.add(table);
            document.add(new Paragraph("\nTotal de l'argent : _________________").setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("\nSignature : _________________").setTextAlignment(TextAlignment.LEFT));

            document.close();

            // Retour du PDF en tant que tableau de octets
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // "inline" permet d'ouvrir dans l'onglet au lieu de télécharger directement
            headers.setContentDisposition(ContentDisposition.inline().filename("requisition.pdf").build());

            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
