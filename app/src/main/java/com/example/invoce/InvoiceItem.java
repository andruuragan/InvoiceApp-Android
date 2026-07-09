package com.example.invoce;

import com.example.invoce.model.Product;

public class InvoiceItem {

    private final Product product;
    private int quantity;


    public InvoiceItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }


    public Product getProduct() {
        return product;
    }


    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}