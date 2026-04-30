package com.pharmacy.pharmacySystem.sales;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SalesRepository extends CrudRepository<Sales, Integer> {
    //Méthode pour compter les ventes
    public Long countByIdSale(Integer idSale);
    // Méthode pour compter les ventes par l'ID de la facture
    @Query("SELECT COUNT(s) FROM Sales s WHERE s.invoice.idInvoice = :invoiceId")
    Long countByInvoiceId(@Param("invoiceId") Integer invoiceId);

    @Query("SELECT s FROM Sales s WHERE s.user.id = :userId AND DATE(s.dateVente) = :date")
    List<Sales> findByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

    //Trier les ventes par date de validation descendant
    @Query("SELECT s FROM Sales s ORDER BY s.dateVente DESC")
    //List<Stock> findAllOrderByDrugNameAsc();
    List<Sales> findAllSalesByDateSaleDesc();
    
    //Calculer le total des ventes quotidiennes
    /**@Query("SELECT  CAST(s.dateVente AS localdate), SUM(s.prixTotal) " +
            "FROM Sales s " +
            "GROUP BY CAST(s.dateVente AS localdate)")
    List<Object[]> getDailySalesTotals();**/

    //Calculer le total des ventes du jour
    @Query("SELECT COALESCE(SUM(s.prixTotal), 0)" +
            "FROM Sales s " +
            "WHERE CAST(s.dateVente AS localdate) = :today")
    BigDecimal getTodaySales(@Param("today") LocalDate today);

    @Query("SELECT s FROM Sales s WHERE DATE(s.dateVente) = :date ORDER BY s.dateVente DESC")
    List<Sales> findByDateVente(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(s.prixTotal), 0) FROM Sales s WHERE DATE(s.dateVente) = :date")
    BigDecimal getSalesTotalByDate(@Param("date") LocalDate date);

    // Trouve les ventes entre deux dates/heures
    List<Sales> findByDateVenteBetween(LocalDateTime start, LocalDateTime end);

    // Compte le nombre de ventes aujourd'hui
    @Query("SELECT COUNT(s) FROM Sales s WHERE FUNCTION('DATE', s.dateVente) = CURRENT_DATE")
    long countSalesToday();

    // Récupère les médicaments les plus vendus (Top 5)
    @Query("SELECT s.drug.nameDrug, SUM(s.quantity) as total FROM Sales s " +
            "GROUP BY s.drug.nameDrug ORDER BY total DESC")
    List<Object[]> findTopSellingDrugs(Pageable pageable);

}
