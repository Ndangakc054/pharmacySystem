package com.pharmacy.pharmacySystem;

import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.drugs.DrugsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class DrugsRepositoryTest {
    @Autowired private DrugsRepository repo;

    @Test
    public void testAddNew() {
        Drugs drug = new Drugs();
        drug.setNameDrug("Para Denk");
        drug.setCategorie("Comprime");
        drug.setUniteDeMesure("Boite");
        drug.setQuantiteParUnite(1);
        /**drug.setPrixUnitaire(new BigDecimal("60000"));
        drug.setDateExpiration(LocalDate.of(2026, 1, 1));**/

        Drugs savedDrug = repo.save(drug);

        Assertions.assertThat(savedDrug).isNotNull();
        Assertions.assertThat(savedDrug.getNameDrug()).isEqualTo("Para Denk");
    }
    @Test
    public void testListAll() {
        Iterable<Drugs> drugs = repo.findAll();
        Assertions.assertThat(drugs).hasSizeGreaterThan(0);
        for (Drugs drug : drugs) {
            System.out.println(drug);
        }
    }
    /**@Test
    public void testUpdate(){
        String drugName = "Acclav";
        Optional<Drugs> optionalDrugs = repo.findById(drugName);
        Drugs drug = optionalDrugs.get();
        drug.setPrixUnitaire(new BigDecimal(25000.00));
        repo.save(drug);
        Drugs updateDrugs = repo.findById(drugName).get();
        Assertions.assertThat(updateDrugs.getPrixUnitaire()).isEqualTo(new BigDecimal(25000.00));
    }
    @Test
    public  void testGet() {
        String drugName = "Dermobact";
        Optional<Drugs> drug = repo.findById(drugName);
        Assertions.assertThat(drug).isPresent();
        System.out.println(drug.get());
    }
    @Test
    public  void testDelete () {
        String nameDrug = "Para Denk";
        repo.deleteById(nameDrug);
        Optional<Drugs> drug = repo.findById(nameDrug);
        Assertions.assertThat(drug).isNotPresent();
    }**/
}
