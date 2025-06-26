package com.pharmacy.pharmacySystem.orders;

import java.time.LocalDate;
import java.util.List;

public class OrdersForm {
    private LocalDate dateOrders;
    private List<OrdersItem> items;

    public LocalDate getDateOrders() {
        return dateOrders;
    }

    public void setDateOrders(LocalDate dateOrders) {
        this.dateOrders = dateOrders;
    }

    public List<OrdersItem> getItems() {
        return items;
    }

    public void setItems(List<OrdersItem> items) {
        this.items = items;
    }
}
