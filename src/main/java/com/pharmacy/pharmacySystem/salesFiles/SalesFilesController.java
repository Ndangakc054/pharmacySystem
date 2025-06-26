package com.pharmacy.pharmacySystem.salesFiles;

import com.itextpdf.io.exceptions.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
        public class SalesFilesController {
    @Autowired SalesFilesService service;


    @PostMapping(value = "/generate-from-html")
    public ResponseEntity<byte[]> generatePdfFromHtml(@RequestBody HtmlPayload payload) {
        try {
            byte[] pdf = service.generateRecentSalePdf(payload.getHtml());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=vente.pdf")
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**@CrossOrigin(origins = "http://localhost:8082")
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody List<SalesFilesDTO> salesData) {
        try {
            byte[] pdfBytes = service.generateAndSaveSalesPdf(salesData);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=fiche_ventes.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }**/
}
