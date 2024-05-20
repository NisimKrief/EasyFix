package com.example.easyfix;

import java.util.ArrayList;

public class Organization {
    private String OrganizationName;
    private String KeyId; // מפתח מתאים של הארגון
    private ArrayList<Building> OrganizationBuildings;
    private User Users;
    private Report Reports;

    public Organization() {
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

    public Report getReports() {
        return Reports;
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

    public User getUsers() {
        return Users;
    }

    public void setUsers(User users) {
        Users = users;
    }

    public void setReports(Report reports) {
        Reports = reports;
    }
}

/*
שם הארגון - מסוג String
מפתח (KeyId) - מסוג String, מתקבל מFireBase
המתחמים של הארגון - מסוג (ArrayList(Building

 */
