package com.pharmacy.pharmacySystem.purchaseFiles;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PurchaseFilesRepository extends CrudRepository<PurchaseFiles, Integer> {
    //Methode pour compter lzs factures par ID
    public Long countByIdFile(Integer idFile);
    //Methode pour trouver un ficjier par son ID
    //Optional<PurchaseFiles> findById(Integer idFile);
}
