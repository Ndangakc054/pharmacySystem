package com.pharmacy.pharmacySystem.salesFiles;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface SalesFilesRepository extends CrudRepository<SalesFiles, BigInteger> {
}
