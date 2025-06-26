package com.pharmacy.pharmacySystem.suppliers;

import com.pharmacy.pharmacySystem.drugs.Drugs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SuppliersRepository extends CrudRepository<Suppliers, Integer> {
    public Long countByIdSupplier(Integer idSupplier);
    Optional<Suppliers> findByIdSupplier(Integer idSupplier);

    //Tri par nom de fournisseur ascendant
    @Query("SELECT s FROM Suppliers s ORDER BY s.nomFournisseur ASC")
    List<Suppliers> findAllOrderBySupplierNameAsc();
}