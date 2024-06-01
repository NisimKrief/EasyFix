package com.example.easyfix.Classes;

import java.util.ArrayList;

public class Organization {
    private String OrganizationName;
    private String KeyId; // מפתח מתאים של הארגון
    private ArrayList<Building> OrganizationBuildings;

    public Organization() {
    }
    public Organization(String organizationName) {
        //For the hint organization
        OrganizationName = organizationName;

    }
    public Organization(String organizationName, String keyId, ArrayList<Building> organizationBuildings) {
        OrganizationName = organizationName;
        KeyId = keyId;
        OrganizationBuildings = organizationBuildings;

    }


    public String getOrganizationName() {
        return OrganizationName;
    }

    public void setOrganizationName(String organizationName) {
        OrganizationName = organizationName;
    }

    public String getKeyId() {
        return KeyId;
    }

    public void setKeyId(String keyId) {
        KeyId = keyId;
    }

    public ArrayList<Building> getOrganizationBuildings() {
        return OrganizationBuildings;
    }

    public void setOrganizationBuildings(ArrayList<Building> organizationBuildings) {
        OrganizationBuildings = organizationBuildings;
    }

}

/*
שם הארגון - מסוג String
מפתח (KeyId) - מסוג String, מתקבל מFireBase
המתחמים של הארגון - מסוג (ArrayList(Building

 */
