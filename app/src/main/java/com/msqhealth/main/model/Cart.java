package com.msqhealth.main.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sihlemabaleka on 9/20/17.
 */

public class Cart {

    List<Map<String, Object>> items;
    String userID;
    boolean assign = false;
    double subtotal;
    private Product product;
    private int quantity;
    long timeStamp;
    String invoice_number;

    public Cart() {
    }

    public Cart(Product product) {
        this.product = product;
    }

    public Cart(String userID, boolean assign, double subtotal, Product product, long timeStamp, String invoice_number) {
        this.userID = userID;
        this.assign = assign;
        this.subtotal = subtotal;
        this.product = product;
        this.timeStamp = timeStamp;
        this.invoice_number = invoice_number;
    }

    public Cart(String userID, double subtotal, Product product, long timeStamp, String invoice_number) {
        this.userID = userID;
        this.subtotal = subtotal;
        this.product = product;
        this.timeStamp = timeStamp;
        this.invoice_number = invoice_number;
    }

    public Cart(long timeStamp, String invoice_number, double subtotal) {
        this.timeStamp = timeStamp;
        this.invoice_number = invoice_number;
        this.subtotal = subtotal;
    }

    public Cart(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }


    public String getInvoice_number() {
        return invoice_number;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("cart-items", items);
        result.put("subtotal", subtotal);
        result.put("assign", assign);
        result.put("invoice_number", invoice_number);
        result.put("timeStamp", ServerValue.TIMESTAMP);

        return result;
    }

}