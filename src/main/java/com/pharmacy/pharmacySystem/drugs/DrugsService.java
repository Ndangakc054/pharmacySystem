package com.pharmacy.pharmacySystem.drugs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.pharmacySystem.audit.AuditService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DrugsService {
    //@Autowired private DrugsRepository repo;
   //@Autowired private ActionsHistoryService historyService;
    private final DrugsRepository repo;
    private final AuditService auditService;
    private final EntityManager manager;
    private final ObjectMapper mapper;

    public DrugsService(DrugsRepository repo, AuditService auditService,
                        EntityManager manager, ObjectMapper mapper) {
        this.repo = repo;
        this.auditService = auditService;
        this.manager = manager;
        this.mapper = mapper;
    }
    public List<Drugs> listAll() {
        return (List<Drugs>) repo.findAllOrderByDrugNameAsc();
    }
    @Transactional
    public Drugs save(Drugs drug) throws DrugsNotFoundException {
        boolean isCreation  = drug.getIdDrug() == null;
        Drugs saveDrug = repo.save(drug);
        try {
            auditService.logAction(
                    "Médicaments",
                    isCreation ? "INSERT" : "UPDATE",
                    saveDrug.getIdDrug(),
                    isCreation ? null : repo.findById(drug.getIdDrug()).orElse(null),
                    saveDrug
            );
        } catch (Exception e) {
            System.err.println("Erreur d'audit: "+ e.getMessage());
        }
        return saveDrug;
       /** boolean isCreate = !repo.existsById(drug.getNameDrug());

        if (isCreate) {
            Drugs savedDrug = repo.save(drug);
            historyService.logInsert("médicaments", savedDrug);
        } else {
            Drugs oldDrug = repo.findById(drug.getNameDrug()).orElseThrow();
            Drugs savedDrug = repo.save(drug);
            historyService.logUpdate("médicaments", oldDrug, savedDrug);
        }**/
      /** if (drug.getNameDrug() == null || drug.getNameDrug().trim().isEmpty()) {
           throw new IllegalMonitorStateException("Le nom du médicament ne peut pas être null ou vide");
       }**/
      //IMPORTANT
      /** boolean isCreation = drug.getIdDrug() == null;

       Drugs oldDrug = null;

       if (!isCreation) {
           // Détacher l'entité pour éviter la récupération du cache
           manager.detach(drug);
           oldDrug = repo.findById(drug.getIdDrug())
                   .orElseThrow(() -> new DrugsNotFoundException("Médicament non trouvé: " +drug.getIdDrug()));
       }
       Drugs savedDrug = repo.save(drug);

       //Journalisation
        auditService.logAction(
                "Médicaments",
                isCreation ? "INSERT" : "UPDATE",
                savedDrug.getIdDrug(),
                oldDrug,
                savedDrug
        );
            return savedDrug;**/
    }
    public Drugs get(Integer idDrug) throws DrugsNotFoundException {
        Optional<Drugs> result = repo.findById(idDrug);
        if(result.isPresent()){
            return result.get();
        }
        throw new DrugsNotFoundException("Aucun médicament n'a été trouvé avec l'identifiant :" + idDrug);
    }
    @Transactional
    public void delete(Integer idDrug) throws DrugsNotFoundException {
       //2
        /**Drugs drugToDelete = repo.findById(nameDrug)
                .orElseThrow(() -> new DrugsNotFoundException("Aucun médicament n'a été trouvé avec l'identifiant :" + nameDrug));
        repo.deleteById(nameDrug);
        historyService.logDelete("médicaments", drugToDelete);**/
        /**Long count = repo.countByNameDrug(nameDrug);
        if (count == null || count == 0){
            throw new DrugsNotFoundException("Aucun médicament n'a été trouvé avec l'identifiant :" +nameDrug);
        }
        repo.deleteById(nameDrug);**/
        Drugs drug = repo.findById(idDrug)
                .orElseThrow(() -> new DrugsNotFoundException(("Aucun médicament trouvé avec l'identifiant: " +idDrug)));
        //Journalisation avant suppresion
        auditService.logAction(
                "Médicaments",
                "DELETE",
                drug.getIdDrug(),
                drug,
                null
        );
        repo.delete(drug);
    }
    // Méthode utilitaire pour récupérer un médicament détaché (optionnel)
    @Transactional(readOnly = true)
    private Drugs getDetachedDrug(Integer idDrug) throws DrugsNotFoundException {
        Drugs drug = repo.findById(idDrug)
                .orElseThrow(() -> new DrugsNotFoundException("Médicament non trouvé: " + idDrug));
        manager.detach(drug);
        return drug;
    }
}
