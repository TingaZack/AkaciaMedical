package com.msqhealth.main.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sihlemabaleka on 7/8/17.
 */

@IgnoreExtraProperties
public class FeaturedItem {

    public String title;
    public String image_url;

    public FeaturedItem() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public FeaturedItem(String title, String image_url) {
        this.title = title;
        this.image_url = image_url;
    }

}