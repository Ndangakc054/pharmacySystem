package com.pharmacy.pharmacySystem.sales;

import java.time.LocalDateTime;
import java.util.List;

public class SalesForm {
    private Integer userId;
    private String userName;
    private LocalDateTime dateVente = LocalDateTime.now();
    private List<SalesItem> items;


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDateTime dateVente) {
        this.dateVente = dateVente;
    }

    public List<SalesItem> getItems() {
        return items;
    }

    public void setItems(List<SalesItem> items) {
        this.items = items;
    }
}
