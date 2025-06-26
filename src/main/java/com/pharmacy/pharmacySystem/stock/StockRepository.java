package com.pharmacy.pharmacySystem.stock;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
}
