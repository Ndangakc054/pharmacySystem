package com.pharmacy.pharmacySystem.invoices;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InvoicesRepository extends CrudRepository<Invoices, Integer> {
    // Méthode pour compter les factures par ID
    public Long countByIdInvoice(Integer idInvoice);

    //Trier les ventes par date de validation descendant
    @Query("SELECT s FROM Invoices s ORDER BY s.dateVente DESC")
    List<Invoices> findAllInvoicesByDateSaleDesc();

    @Query("SELECT i FROM Invoices i ORDER BY i.idInvoice DESC LIMIT 1")
    Invoices findTopByOrderByIdInvoiceDesc();

    // Compte le nombre de factures  aujourd'hui
    @Query("SELECT COUNT(i) FROM Invoices i WHERE FUNCTION('DATE', i.dateVente) = CURRENT_DATE")
    long countInvoicesToday();
}
