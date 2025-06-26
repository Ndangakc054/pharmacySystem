package com.pharmacy.pharmacySystem.suppliers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.pharmacySystem.audit.AuditService;
import com.pharmacy.pharmacySystem.drugs.Drugs;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuppliersService {
   // @Autowired private SuppliersRepository repo;
   // @Autowired private ActionsHistoryService historyService;
    private final SuppliersRepository repo;
    private final AuditService auditService;
    private final EntityManager manager;
    private final ObjectMapper mapper;

    public SuppliersService(SuppliersRepository repo, AuditService auditService,
                            EntityManager manager, ObjectMapper mapper) {
        this.repo = repo;
        this.auditService = auditService;
        this.manager = manager;
        this.mapper = mapper;
    }
    public List<Suppliers> listAll() {
        return (List<Suppliers>) repo.findAllOrderBySupplierNameAsc();
    }
    @Transactional
    public Suppliers save(Suppliers supplier) throws SuppliersNotFoundException{
       /** boolean isNew = !repo.existsById(supplier.getNomFournisseur());
        if (isNew) {
            Suppliers saved = repo.save(supplier);
            historyService.logInsert("fournisseurs", saved);
        } else {
            Suppliers old = repo.findById(supplier.getNomFournisseur()).orElseThrow(null);
            Suppliers saved = repo.save(supplier);
            historyService.logUpdate("fourniseurs", old, saved);
        }**/
       boolean isCreation = supplier.getIdSupplier() == null;
       Suppliers oldSupplier = null;

       if (!isCreation) {
           manager.detach(supplier);
           oldSupplier = repo.findById(supplier.getIdSupplier())
                   .orElseThrow(() -> new SuppliersNotFoundException("Fournisseur non trouvé : " +supplier.getIdSupplier()));
       }
       Suppliers savedSupplier = repo.save(supplier);

       //Jpurnalisation
        auditService.logAction(
                "Fournisseurs",
                isCreation ? "AJOUT" : "MIS À JOUR",
                savedSupplier.getIdSupplier(),
                oldSupplier,
                savedSupplier
        );
        return savedSupplier;
    }
    public Suppliers get(Integer idSupplier) throws SuppliersNotFoundException {
        Optional<Suppliers> result = repo.findById(idSupplier);
        if (result.isPresent()){
            return result.get();
        }
        throw new SuppliersNotFoundException("Aucun fournisseur n'a été trouvé avec l'identification :" +idSupplier);
    }
    @Transactional
    public void delete(Integer idSupplier) throws SuppliersNotFoundException {
       //2
        /** Suppliers supplier = repo.findById(nomFournisseur)
                .orElseThrow(()-> new SuppliersNotFoundException("Aucun fournisseur n'a été trouvé avec l'identification :" +nomFournisseur));
        repo.deleteById(nomFournisseur);
        historyService.logDelete("fournisseurs", supplier);**/
        //1
        /**Long count = repo.countByIdSupplier(idSupplier);
        if(count == null || count == 0){
            throw new SuppliersNotFoundException("Aucun fournisseur n'a été trouvé avec l'identification :" +idSupplier);
        }
        repo.deleteById(idSupplier);**/
        Suppliers supplier = repo.findById(idSupplier)
                .orElseThrow(() -> new SuppliersNotFoundException("Aucun fournisseur trouvé avec l'identifiant: " +idSupplier));
        //Journalisation de la suppression
        auditService.logAction(
                "Fournisseurs",
                "SUPPRESSION",
                supplier.getIdSupplier(),
                supplier,
                null
        );
        repo.delete(supplier);
    }
}
