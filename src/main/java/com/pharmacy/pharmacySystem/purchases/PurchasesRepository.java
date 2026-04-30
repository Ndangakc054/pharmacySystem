package com.pharmacy.pharmacySystem.purchases;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PurchasesRepository extends CrudRepository<Purchases, Integer> {
    public Long countByIdPurchase(Integer idPurchase);

    //Trier les achats par date d'entrée descendant
    @Query("SELECT s FROM Purchases s ORDER BY s.dateAchat DESC")
    List<Purchases> findAllPurchasesByDatePurchaseDesc();

    //Calculer le total des achats du jour
    @Query("SELECT COALESCE(SUM(s.prixAchatTotal), 0)" +
            "FROM Purchases s " +
            "WHERE CAST(s.dateAchat AS localdate) = :today")
    BigDecimal getTodayPurchases(@Param("today") LocalDate today);

    @Query("SELECT p FROM Purchases p WHERE DATE(p.dateAchat) = :date ORDER BY p.dateAchat DESC")
    List<Purchases> findByDateAchat(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(p.prixAchatTotal), 0) FROM Purchases p WHERE DATE(p.dateAchat) = :date")
    BigDecimal getPurchasesTotalByDate(@Param("date") LocalDate date);

    // Compte le nombre d'achats aujourd'hui
    @Query("SELECT COUNT(p) FROM Purchases p WHERE p.dateAchat = CURRENT_DATE")
    long countPurchasesToday();

    /**@Query("SELECT COALESCE(SUM(p.prixAchatTotal), 0) " +
            "FROM Purchases p " +
            "WHERE CAST(p.dateAchat AS localdate) = :date")
    BigDecimal getPurchasesTotalByDate(@Param("date") LocalDate date);**/

    /**@Query("SELECT COALESCE(SUM(p.prixAchatTotal), 0) FROM Purchases p WHERE DATE(p.dateAchat) = :date")
    BigDecimal getPurchasesTotalByDate(@Param("date") LocalDate date);**/


    /**@Query("SELECT s FROM Purchases s WHERE DATE(s.dateAchat) = :date ORDER BY s.dateAchat DESC")
    List<Purchases> findByDateAchat(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(s.prixAchatTotal), 0) FROM Purchases s WHERE DATE(s.dateAchat) = :date")
    BigDecimal getPurchasesTotalByDate(@Param("date") LocalDate date);**/
}
