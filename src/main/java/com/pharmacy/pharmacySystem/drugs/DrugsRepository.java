package com.pharmacy.pharmacySystem.drugs;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DrugsRepository extends CrudRepository<Drugs, Integer> {
     /**Long countByNameDrug(String nameDrug);
     Optional<Drugs> findByNameDrug(String nameDrug);**/
     public Long countByIdDrug(Integer idDrug);
     Optional<Drugs> findById(Integer id);
     //Optional<Drugs> findByNameDrug(String nameDrug);

     //Tri par nom de médicament ascendant
     @Query("SELECT s FROM Drugs s ORDER BY s.nameDrug ASC")
     List<Drugs> findAllOrderByDrugNameAsc();
}
