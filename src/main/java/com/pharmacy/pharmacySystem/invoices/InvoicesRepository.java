package com.pharmacy.pharmacySystem.invoices;

import org.springframework.data.repository.CrudRepository;

public interface InvoicesRepository extends CrudRepository<Invoices, Integer> {
    // Méthode pour compter les factures par ID
    public Long countByIdInvoice(Integer idInvoice);
}
