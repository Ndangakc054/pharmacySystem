package com.pharmacy.pharmacySystem.stock;

import java.time.LocalDate;

public class StockExpirationDTO {
    private Integer id;
    private String drugName;
    private Integer quantity;
    private LocalDate expirationDate;
    private String status;
    private String remainingTime;
    private Integer daysRemaining;

    public StockExpirationDTO() {

    }
    public StockExpirationDTO(Integer id, String drugName, Integer quantity, LocalDate expirationDate, String status, String remainingTime, Integer daysRemaining) {
        this.id = id;
        this.drugName = drugName;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.status = status;
        this.remainingTime = remainingTime;
        this.daysRemaining = daysRemaining;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Integer getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Integer daysRemaining) {
        this.daysRemaining = daysRemaining;
    }
}
