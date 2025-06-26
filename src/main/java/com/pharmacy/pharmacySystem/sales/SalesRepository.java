package com.pharmacy.pharmacySystem.sales;

import com.pharmacy.pharmacySystem.salesFiles.SalesFiles;
import com.pharmacy.pharmacySystem.stock.Stock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

}
