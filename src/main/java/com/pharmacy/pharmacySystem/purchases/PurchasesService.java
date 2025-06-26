package com.pharmacy.pharmacySystem.purchases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.pharmacySystem.audit.AuditService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PurchasesService {
    //@Autowired private PurchasesRepository repo;
    //@Autowired private ActionsHistoryService historyService;
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
}
