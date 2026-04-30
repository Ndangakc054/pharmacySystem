package com.pharmacy.pharmacySystem.purchases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.pharmacySystem.audit.AuditService;
import com.pharmacy.pharmacySystem.stock.Stock;
import com.pharmacy.pharmacySystem.stock.StockRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PurchasesService {
    //@Autowired private PurchasesRepository repo;
    //@Autowired private ActionsHistoryService historyService;
    @Autowired private StockRepository stockRepo;
    private final PurchasesRepository repo;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

    public PurchasesService(PurchasesRepository repo, AuditService auditService, ObjectMapper objectMapper, EntityManager entityManager) {
        this.repo = repo;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
        this.entityManager = entityManager;
    }

    public List<Purchases> listAll() {
        return (List<Purchases>) repo.findAllPurchasesByDatePurchaseDesc();
    }

    /**@Transactional
    public Purchases savePurchaseWithInventoryUpdate(Purchases purchase) {
        // 1. Sauvegarder l'achat
        Purchases savedPurchase = repo.save(purchase);

        // 2. Vérifier si un batch existe déjà pour ce médicament et numéro de lot
        Optional<Batches> existingBatch = batchesRepo.findByDrugIdAndBatchNumber(
                purchase.getDrug().getIdDrug(),
                purchase.getBatchNumber()
        );
        Batches batch;
        if (existingBatch.isPresent()) {
            //Batch existant - Mise à jour
            batch = existingBatch.get();
            batch.setQuantity(batch.getQuantity() + purchase.getQuantity());
            batchesRepo.save(batch);

            // Mettre à jour le stock correspondant
            updateExixstingStock(batch, purchase);
        } else {
            //Nouveau batch - Création
            batch = createNewBatch(purchase);
            batchesRepo.save(batch);

            // Créer une nouvelle entrée dans le stock
            //createNewStockEntry(newBatch, purchase);

            //Mettre à jour le stock (toujours incrémenter la quantité)
            updateExixstingStock(batch,purchase);

        }
        return savedPurchase;
    }**/

    @Transactional
    public Purchases save(Purchases purchase) {
        /**boolean isNew = purchase.getId() == null;
        if (isNew) {
            Purchases saved = repo.save(purchase);
            historyService.logInsert("achats", saved);
        } else {
            Purchases old = repo.findById(purchase.getId()).orElse(null);
            Purchases saved = repo.save(purchase);
            historyService.logUpdate("achats", old, saved);
        }**/
        boolean isCreation = purchase.getIdPurchase() == null;
        Purchases oldPurchase = null;

        if (!isCreation) {
            entityManager.detach(purchase);
            oldPurchase = repo.findById(Integer.valueOf(purchase.getIdPurchase())).orElse(null);
        }
        Purchases purchaseSaving = repo.save(purchase);
        //Journalisation
        auditService.logAction(
                "Achats",
                isCreation ? "AJOUT" : "MIS À JOUR",
                purchaseSaving.getIdPurchase(),
                oldPurchase,
                purchaseSaving
        );
        return purchaseSaving;
        //repo.save(purchase);
    }
    public Purchases get(Integer idPurchase) throws PurchasesNotFounfException {
        Optional<Purchases> result = repo.findById(idPurchase);
        if (result.isPresent()) {
            return result.get();
        }
        throw new PurchasesNotFounfException("Aucun achat n'a été trouvé avec l'identification :" +idPurchase);
    }
    @Transactional
    public void delete(Integer idPurchase) throws PurchasesNotFounfException {
       //2
        /** Purchases purchase = repo.findById(Id)
                .orElseThrow(()-> new PurchasesNotFounfException("Aucun achat n'a été trouvé avec l'identification : " +Id));
        repo.deleteById(Id);
        historyService.logDelete("achats", purchase);**/
        //1
        /**Long count = repo.countByIdPurchase(idPurchase);
        if (count == null || count == 0) {
            throw new PurchasesNotFounfException("Aucun achat n'a été trouvé avec l'identification :" +idPurchase);
        }
        repo.deleteById(idPurchase);**/
        Purchases purchase = repo.findById(idPurchase)
                .orElseThrow(()-> new PurchasesNotFounfException("Aucun achat n'a été trouvé avec l'identification : " +idPurchase));

        //Journalisation avant la suppression
        auditService.logAction(
                "Achats",
                "SUPPRESSION",
                purchase.getIdPurchase(),
                purchase,
                null
        );
        repo.delete(purchase);
    }
    //SERVICE POUR LE TOTAL DES ACHATS DU JOUR
    public BigDecimal getTodayPurchasesTotal(){
        return repo.getTodayPurchases(LocalDate.now());
    }

    public BigDecimal getPurchasesTotalByDate(LocalDate date) {
        return repo.getPurchasesTotalByDate(date);
    }

    public List<Purchases> getPurchasesByDate(LocalDate date) {
        return repo.findByDateAchat(date);
    }

    /**public BigDecimal getPurchasesTotalByDate(LocalDate date) {
        return repo.getPurchasesTotalByDate(date);
    }

    public List<Purchases> getPurchasesByDate(LocalDate date){
        return repo.findByDateAchat(date);
    }**/

}
