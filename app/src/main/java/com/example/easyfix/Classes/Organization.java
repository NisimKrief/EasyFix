package com.example.easyfix.Classes;

import java.util.ArrayList;

/**
 * The Object Organization
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	08/02/2024
 * This class represents an organization with its associated details such as name, buildings, and classes.
 */
public class Organization {
    private String OrganizationName;
    private String KeyId; // מפתח מתאים של הארגון
    private ArrayList<Building> OrganizationBuildings;
    private ArrayList<String> classesOrWorkArea;

    /**
     * Instantiates a new Organization.
     */
    public Organization() {
    }

    /**
     * Instantiates a new Organization with a specified name.
     * hint organization builder for the organizations spinner
     *
     * @param organizationName the name of the organization
     */
    public Organization(String organizationName) {
        //For the hint organization
        OrganizationName = organizationName;
        classesOrWorkArea = new ArrayList<String>();
        classesOrWorkArea.add("אופציות אלו יופיעו אחרי בחירת מוסד.");

    }

    /**
     * Instantiates a new Organization with specified details.
     *
     * @param organizationName      the name of the organization
     * @param keyId                 the key ID of the organization
     * @param organizationBuildings the list of buildings in the organization
     * @param classesOrWorkArea     the list of classes or work areas in the organization
     */
    public Organization(String organizationName, String keyId, ArrayList<Building> organizationBuildings, ArrayList<String> classesOrWorkArea) {
        OrganizationName = organizationName;
        KeyId = keyId;
        OrganizationBuildings = organizationBuildings;
        this.classesOrWorkArea = classesOrWorkArea;

    }

    /**
     * Gets the classes or work areas of the organization.
     *
     * @return the list of classes or work areas
     */
    public ArrayList<String> getClassesOrWorkArea() {
        return classesOrWorkArea;
    }

    /**
     * Sets the classes or work areas of the organization.
     *
     * @param classesOrWorkArea the new list of classes or work areas
     */
    public void setClassesOrWorkArea(ArrayList<String> classesOrWorkArea) {
        this.classesOrWorkArea = classesOrWorkArea;
    }

    /**
     * Gets the name of the organization.
     *
     * @return the name of the organization
     */
    public String getOrganizationName() {
        return OrganizationName;
    }

    /**
     * Sets the name of the organization.
     *
     * @param organizationName the new name of the organization
     */
    public void setOrganizationName(String organizationName) {
        OrganizationName = organizationName;
    }

    /**
     * Gets the key ID of the organization.
     *
     * @return the key ID of the organization
     */
    public String getKeyId() {
        return KeyId;
    }

    /**
     * Sets the key ID of the organization.
     *
     * @param keyId the new key ID of the organization
     */
    public void setKeyId(String keyId) {
        KeyId = keyId;
    }

    /**
     * Gets the list of buildings in the organization.
     *
     * @return the list of buildings
     */
    public ArrayList<Building> getOrganizationBuildings() {
        return OrganizationBuildings;
    }

    /**
     * Sets the list of buildings in the organization.
     *
     * @param organizationBuildings the new list of buildings
     */
    public void setOrganizationBuildings(ArrayList<Building> organizationBuildings) {
        OrganizationBuildings = organizationBuildings;
    }

}

