package com.pharmacy.pharmacySystem.stock;

public class StockNotFoundException extends Throwable {
    public StockNotFoundException(String message) {
        super(message);
    }
}
