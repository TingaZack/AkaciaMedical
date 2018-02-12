package com.android.msqhealthpoc1.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sihlemabaleka on 9/20/17.
 */

public class Cart {

    List<Map<String, Object>> items;
    String userID;
    double subtotal;

    public Cart() {
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


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("cart-items", items);
        result.put("subtotal", subtotal);

        return result;
    }

}