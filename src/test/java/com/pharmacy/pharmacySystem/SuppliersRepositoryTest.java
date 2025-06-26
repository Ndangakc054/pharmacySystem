package com.pharmacy.pharmacySystem;

import com.pharmacy.pharmacySystem.suppliers.Suppliers;
import com.pharmacy.pharmacySystem.suppliers.SuppliersRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigInteger;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class SuppliersRepositoryTest {
    @Autowired private SuppliersRepository repo;
    @Test
    public void testAddNew() {
        Suppliers supplier = new Suppliers();
        supplier.setNomFournisseur("MultiPhar");
        supplier.setNif(new BigInteger("4000001174"));
        supplier.setAdresse("13 AV.DE LA VICTOIRE");
        supplier.setEmail("contact@multiphar.co.bi");

        Suppliers saveSupplier = repo.save(supplier);

        Assertions.assertThat(saveSupplier).isNotNull();
        Assertions.assertThat(saveSupplier.getNomFournisseur()).isEqualTo("MultiPhar");
    }

}

