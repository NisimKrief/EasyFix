package com.example.easyfix;

import java.util.ArrayList;

public class Organization {
    String OrganizationName;
    String KeyId; // מפתח מתאים של הארגון
    ArrayList<Building> OrganizationBuildings;

    public Organization(String organizationName, String keyId, ArrayList<Building> organizationBuildings) {
        OrganizationName = organizationName;
        KeyId = keyId;
        OrganizationBuildings = organizationBuildings;
    }
}

/*
שם הארגון - מסוג String
מפתח (KeyId) - מסוג String, מתקבל מFireBase
המתחמים של הארגון - מסוג (ArrayList(Building

 */
