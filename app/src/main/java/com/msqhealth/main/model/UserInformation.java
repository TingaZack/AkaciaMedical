package com.msqhealth.main.model;

/**
 * Created by sihlemabaleka on 7/10/17.
 */

public class UserInformation {

    public String occupation;
    public String name;

    public UserInformation() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserInformation(String occupation, String name) {
        this.occupation = occupation;
        this.name = name;
    }

}

