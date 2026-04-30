package com.pharmacy.pharmacySystem.sales;

import java.math.BigDecimal;

public class SalesReportItem {
    private String drugName;
    private Integer totalQuantity = 0;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private BigDecimal unitPrice; // On garde le dernier prix unitaire ou le fixe

    public SalesReportItem(String drugName, BigDecimal unitPrice) {
        this.drugName = drugName;
        this.unitPrice = unitPrice;
    }

    public void addSale(Integer quantity, BigDecimal rowTotal) {
        this.totalQuantity += quantity;
        this.totalRevenue = this.totalRevenue.add(rowTotal);
    }

    // Getters simples
    public String getDrugName() { return drugName; }
    public Integer getTotalQuantity() { return totalQuantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
}
