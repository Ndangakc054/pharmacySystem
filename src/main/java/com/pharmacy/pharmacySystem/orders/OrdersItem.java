package com.pharmacy.pharmacySystem.orders;

public class OrdersItem {
    private Integer idDrug;
    private String drugName;
    private Integer quantity;

    public Integer getIdDrug() {
        return idDrug;
    }
    public void setIdDrug(Integer idDrug) {
        this.idDrug = idDrug;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
