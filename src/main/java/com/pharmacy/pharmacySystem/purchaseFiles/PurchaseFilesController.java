package com.pharmacy.pharmacySystem.purchaseFiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigInteger;

@Controller
public class PurchaseFilesController {
    @Autowired private PurchaseFilesService service;

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> getPdf(@PathVariable Integer id) {
        PurchaseFiles file = service.getPurchaseFileById(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .body(file.getPdfOrders());
    }
}
