package com.pharmacy.pharmacySystem;

import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.purchases.Purchases;
import com.pharmacy.pharmacySystem.purchases.PurchasesRepository;
import com.pharmacy.pharmacySystem.suppliers.Suppliers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.time.LocalDate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class PurchasesRepositoryTest {
    @Autowired private PurchasesRepository repo;
    @Test
    public void testAddNew() {
        Drugs drug = new Drugs();
        drug.setNameDrug("Acclav");
        Suppliers supplier = new Suppliers();
        supplier.setNomFournisseur("Lotus");
        Purchases purchase = new Purchases();
        purchase.setDrug(drug);
        purchase.setSupplier(supplier);
        purchase.setQuantity(1);
        purchase.setPrixAchatUnitaire(new BigDecimal("17000"));
        purchase.setPrixAchatTotal(new BigDecimal("17000"));
        purchase.setDateAchat(LocalDate.of(2027,01,01));
        Purchases savePurchase = repo.save(purchase);
        Assertions.assertThat(savePurchase).isNotNull();
        Assertions.assertThat(savePurchase.getIdPurchase()).isGreaterThan(0);
    }
}
