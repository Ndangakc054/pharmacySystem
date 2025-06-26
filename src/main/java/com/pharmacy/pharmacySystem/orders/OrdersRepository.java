package com.pharmacy.pharmacySystem.orders;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrdersRepository extends CrudRepository<Orders, Integer> {
    //Methode pour compter les commandes pat ID
     Long countByidOrder(Integer idOrder);

    //Methode pour compter les commandes par Id dans la fiche de réquisition
    /**@Query("SELECT COUNT(o) FROM Orders o WHERE o.purchaseFiles.idFile = :fileId")
    Long countByFileId(@Param("fileId") Integer fileId);**/
}
