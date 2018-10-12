package com.msqhealth.main.model;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegistrationDetails {

    String Name, Practice_Number, Speciality, Telephone, Suburb, Email;
    boolean verified;
    long Timestamp;

    public RegistrationDetails() {
    }

    public RegistrationDetails(String name, String practice_Number, String speciality, String telephone, String suburb, String email, boolean verified, long timestamp) {
        Name = name;
        Practice_Number = practice_Number;
        Speciality = speciality;
        Telephone = telephone;
        Suburb = suburb;
        Email = email;
        this.verified = verified;
        Timestamp = timestamp;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPractice_Number() {
        return Practice_Number;
    }

    public void setPractice_Number(String practice_Number) {
        Practice_Number = practice_Number;
    }

    public String getSpeciality() {
        return Speciality;
    }

    public void setSpeciality(String speciality) {
        Speciality = speciality;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getSuburb() {
        return Suburb;
    }

    public void setSuburb(String suburb) {
        Suburb = suburb;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Name", Name);
        result.put("Practice_Number", Practice_Number);
        result.put("Speciality", Speciality);
        result.put("Telephone", Telephone);
        result.put("Suburb", Speciality);
        result.put("verified", false);
        result.put("Timestamp", new Date().getTime());

        return result;
    }
}
