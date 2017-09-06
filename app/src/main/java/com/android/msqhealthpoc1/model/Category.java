package com.android.msqhealthpoc1.model;

/**
 * Created by sihlemabaleka on 7/10/17.
 */

public class Category {

    public String title;
    public String image_url;

    public Category() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Category(String title, String image_url) {
        this.title = title;
        this.image_url = image_url;
    }

}