package com.pharmacy.pharmacySystem.purchaseFiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class PurchaseFilesService {
    @Autowired private PurchaseFilesRepository repo;

    public List<PurchaseFiles> listAll() {
        return (List<PurchaseFiles>) repo.findAll();
    }
    public PurchaseFiles getPurchaseFileById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier non trouvé"));
    }
}
