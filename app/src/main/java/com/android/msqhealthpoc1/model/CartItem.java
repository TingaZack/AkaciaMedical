package com.android.msqhealthpoc1.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sihlemabaleka on 9/20/17.
 */

public class CartItem {


    private String owner_id;
    private Product product;
    private int quantity;


    public CartItem() {
    }

    public CartItem(String owner_id, Product product, int quantity) {
        this.owner_id = owner_id;
        this.product = product;
        this.quantity = quantity;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", owner_id);
        result.put("product", product);
        result.put("quantity", quantity);

        return result;
    }
}
