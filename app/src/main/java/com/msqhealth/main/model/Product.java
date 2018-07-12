package com.msqhealth.main.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sihlemabaleka on 7/26/17.
 */

public class Product {


    public String code;
    public String consumables;
    public String description;
    public double price;
    public String unit_of_messuremeant;
    public String percentage;
    public String trueImageUrl;
    public String imageUrl;
    public boolean promotion = false;
    public Map<String, Boolean> stars = new HashMap<>();

    public Product() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Product(String code, String consumables, String description, double price, String unit_of_messuremeant, String trueImageUrl) {
        this.code = code;
        this.consumables = consumables;
        this.description = description;
        this.price = price;
        this.unit_of_messuremeant = unit_of_messuremeant;
        this.trueImageUrl = trueImageUrl;
    }

    public Product(String code, String consumables, String description, double price, String unit_of_messuremeant, String trueImageUrl, boolean promotion) {
        this.code = code;
        this.consumables = consumables;
        this.description = description;
        this.price = price;
        this.unit_of_messuremeant = unit_of_messuremeant;
        this.trueImageUrl = trueImageUrl;
        this.promotion = promotion;
    }

    public Product(String code, String consumables, String description, double price, String unit_of_messuremeant, String percentage, String trueImageUrl, boolean promotion) {
        this.code = code;
        this.consumables = consumables;
        this.description = description;
        this.price = price;
        this.unit_of_messuremeant = unit_of_messuremeant;
        this.percentage = percentage;
        this.trueImageUrl = trueImageUrl;
        this.promotion = promotion;
    }

    public Product(String consumables, String imageUrl) {
        this.consumables = consumables;
        this.imageUrl = imageUrl;
    }

    public Product(String consumables, String description, double price, String imageUrl) {
        this.consumables = consumables;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Product(String code, String consumables, String description, double price, String trueImageUrl) {
        this.code = code;
        this.consumables = consumables;
        this.description = description;
        this.price = price;
        this.trueImageUrl = trueImageUrl;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getConsumables() {
        return consumables;
    }

    public void setConsumables(String consumables) {
        this.consumables = consumables;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUnit_of_messuremeant() {
        return unit_of_messuremeant;
    }

    public void setUnit_of_messuremeant(String unit_of_messuremeant) {
        this.unit_of_messuremeant = unit_of_messuremeant;
    }

    public String getTrueImageUrl() {
        return trueImageUrl;
    }

    public void setTrueImageUrl(String trueImageUrl) {
        this.trueImageUrl = trueImageUrl;
    }

    public Map<String, Boolean> getStars() {
        return stars;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("CODE", code);
        result.put("CONSUMABLES", consumables);
        result.put("DESCRIPTION", description);
        result.put("PRICING", price);
        result.put("PRICING UNIT", unit_of_messuremeant);
        result.put("True Image", trueImageUrl);
        return result;
    }
}
