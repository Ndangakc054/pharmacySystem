package com.pharmacy.pharmacySystem.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.pharmacySystem.audit.AuditService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {
    //@Autowired private StockRepository repo;
    @Autowired private JdbcTemplate jdbcTemplate;
    //@Autowired private AuditService auditService;

    private final StockRepository repo;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

    public StockService(StockRepository repo, AuditService auditService, ObjectMapper objectMapper, EntityManager entityManager) {
        this.repo = repo;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
        this.entityManager = entityManager;
    }
    public List<Stock> listAll() {
        return (List<Stock>) repo.findAllOrderByDrugNameAsc();
    }
    @Transactional
    public Stock save(Stock stock) {
        /**boolean isCreate = stock.getId() == null;
        if (isCreate) {
            Stock savedStock = repo.save(stock);
            historyService.logInsert("stock", savedStock);
        } else {
            Stock oldStock = repo.findById(stock.getId()).orElse(null);
            Stock savedStock = repo.save(stock);
            historyService.logUpdate("stock", oldStock, savedStock);
        }**/
        boolean isCreation = stock.getIdStock() == null;
        Stock oldStock = null;

        if (!isCreation) {
            // Détacher l'entité pour éviter la récupération du cache
            entityManager.detach(stock);
            oldStock = repo.findById(Integer.valueOf(stock.getIdStock())).orElse(null);
        }
        Stock stockSaving = repo.save(stock);
        //Journalisation
        auditService.logAction(
                "stock",
                isCreation ? "AJOUT" : "MIS À JOUR",
                stockSaving.getIdStock(),
                oldStock,
                stockSaving
        );

        return stockSaving;
        //repo.save(stock);
    }
    public Stock get(Integer idStock) throws StockNotFoundException {
        Optional<Stock> result = repo.findById(idStock);
        if (result.isPresent()) {
            return result.get();
        }
        throw new StockNotFoundException("Aucun médicament dans le stock n'a été trouvé avec l'identification :" +idStock);
    }
    @Transactional
    public Stock getStockForAudit(Integer idStock) throws StockNotFoundException {
        return repo.findById(idStock)
                .map(stock -> {
                    Stock detached = new Stock();
                    BeanUtils.copyProperties(stock, detached);
                    return detached;
                })
                .orElseThrow(() -> new StockNotFoundException("Aucun médicament dans le stock n'a été trouvé avec l'identification :" +idStock));
    }
    @Transactional
    public void delete(Integer idStock) throws StockNotFoundException {
        //2
        /**Stock stockDelete = repo.findById(Id)
                .orElseThrow(() -> new StockNotFoundException("Aucun médicament dans le stock n'a été trouvé avec l'identification : " +Id));
        repo.deleteById(Id);
        historyService.logDelete("stock", stockDelete);**/
        //1
        /**Long count = repo.countById(Id);
        if (count == null || count == 0) {
            throw new StockNotFoundException("Aucun médicament dans le stock n'a été trouvé avec l'identification :" +Id);
        }
        repo.deleteById(Id);**/
        Stock stock = repo.findById(idStock)
                .orElseThrow(()-> new StockNotFoundException("Aucun médicament dans le stock n'a été trouvé avec l'identification : " +idStock));

        //Journalisation avant la suppression
        auditService.logAction(
                "stock",
                "SUPPRESSION",
                stock.getIdStock(),
                stock,
                null
        );
        repo.delete(stock);
    }
    public List<StockExpirationDTO> getDrugsExpiration() {
        String sql = "CALL check_expired_medication()";
        return jdbcTemplate.query(sql, (rs, rowNum) ->{
            StockExpirationDTO dto = new StockExpirationDTO();
            dto.setId(rs.getInt("ID"));
            dto.setDrugName(rs.getString("Médicament"));
            dto.setQuantity(rs.getInt("Quantité"));
            dto.setExpirationDate(rs.getDate("Date Expiration").toLocalDate());
            dto.setStatus(rs.getString("Statut"));
            dto.setRemainingTime(rs.getString("Temps Restant"));
            dto.setDaysRemaining(rs.getInt("Jours Restants"));
            return dto;
        });
    }
}
