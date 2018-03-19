package com.msqhealth.main.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sihlemabaleka on 2/9/18.
 */

public class Practice {

    String name, practiceNumber, practitionerID;

    public Practice() {
    }

    public Practice(String name, String practiceNumber, String practitionerID) {
        this.name = name;
        this.practiceNumber = practiceNumber;
        this.practitionerID = practitionerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPracticeNumber() {
        return practiceNumber;
    }

    public void setPracticeNumber(String practiceNumber) {
        this.practiceNumber = practiceNumber;
    }

    public String getPractitionerID() {
        return practitionerID;
    }

    public void setPractitionerID(String practitionerID) {
        this.practitionerID = practitionerID;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("practice_name", name);
        result.put("practice_number", practiceNumber);
        result.put("practitioner_id_number", practitionerID);

        return result;
    }

}
