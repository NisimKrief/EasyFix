package com.example.easyfix.Classes;

import java.util.ArrayList;

public class Organization {
    private String OrganizationName;
    private String KeyId; // מפתח מתאים של הארגון
    private ArrayList<Building> OrganizationBuildings;
    private ArrayList<String> classesOrWorkArea;

    public Organization() {
    }
    public Organization(String organizationName) {
        //For the hint organization
        OrganizationName = organizationName;
        classesOrWorkArea = new ArrayList<String>();
        classesOrWorkArea.add("אופציות אלו יופיעו אחרי בחירת מוסד.");

    }
    public Organization(String organizationName, String keyId, ArrayList<Building> organizationBuildings, ArrayList<String> classesOrWorkArea) {
        OrganizationName = organizationName;
        KeyId = keyId;
        OrganizationBuildings = organizationBuildings;
        this.classesOrWorkArea = classesOrWorkArea;

    }

    public ArrayList<String> getClassesOrWorkArea() {
        return classesOrWorkArea;
    }

    public void setClassesOrWorkArea(ArrayList<String> classesOrWorkArea) {
        this.classesOrWorkArea = classesOrWorkArea;
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

