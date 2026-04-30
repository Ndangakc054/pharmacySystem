package com.pharmacy.pharmacySystem.stock;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends CrudRepository<Stock, Integer> {
    /**public Long countByIdStock(Integer idStock);**/
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.drug.idDrug = :idDrug")
    int getTotalQuantityByDrugName(@Param("idDrug") Integer idDrug);

    //Tri par nom de médicaments ascendant
    @Query("SELECT s FROM Stock s ORDER BY s.nameDrug ASC")
    List<Stock> findAllOrderByDrugNameAsc();

    // Recherche par nom (conservée)
    @Query("SELECT s FROM Stock s WHERE s.nameDrug LIKE %:searchTerm%")
    List<Stock> searchByDrugName(@Param("searchTerm") String searchTerm);


    //TOTAL DE L'INVENTAIRE
    @Query("SELECT COALESCE(SUM(s.prixTotal), 0) FROM Stock s")
    BigDecimal getTotalInventoryValue();

    @Query("SELECT s FROM Stock s WHERE s.drug.idDrug = :drugId AND s.quantity > 0 ORDER BY s.dateExpiration ASC")
        List<Stock> findBatchesByDrugIdOrderByExpDateAsc(
                @Param("drugId") Integer drugId
    );

    /**@Query("SELECT s FROM Stock s WHERE s.batch.idBatch = :batchId")
    Optional<Stock> findByBatchId(
            @Param("batchId") Integer batchId
    );**/
    /**@Query("SELECT s FROM Stock s WHERE s.drug.idDrug = :drugId AND s.batchNumber = :batchNumber")
    Optional<Stock> findByDrugIdAndBatchNumber(
                @Param("drugId") Integer drugId,
                @Param("batchNumber") String batchNumber
        );**/

    /**@Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity - :qty WHERE s.batch = :batchId")
    void deductFromBatch(
                @Param("bacthId") String bacthId,
                @Param("qty") Integer qty
    );**/


}
