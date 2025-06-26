package com.pharmacy.pharmacySystem.purchases;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurchasesRepository extends CrudRepository<Purchases, Integer> {
    public Long countByIdPurchase(Integer idPurchase);

    //Trier les achats par date d'entrée descendant
    @Query("SELECT s FROM Purchases s ORDER BY s.dateAchat DESC")
    List<Purchases> findAllPurchasesByDatePurchaseDesc();
}
