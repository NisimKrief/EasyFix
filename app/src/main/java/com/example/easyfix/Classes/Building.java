package com.example.easyfix.Classes;

import java.util.ArrayList;

/**
 * The Object Building
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	08/02/2024
 * Represents a Building with a name and a list of rooms.
 * This class is used to encapsulate the details of a building in the EasyFix application.
 */
public class Building {
    private String buildingName;
    private ArrayList<String> rooms; // List of rooms in the building

    /**
     * Instantiates a new Building.
     */
    public Building() {
    }

    /**
     * Instantiates a new Building.
     *
     * @param name  the name of the building
     * @param rooms the list of rooms in the building
     */
    public Building(String name, ArrayList<String> rooms) {
        this.buildingName = name;
        this.rooms = rooms;
    }

    /**
     * Gets the building name.
     *
     * @return the building name
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Sets the building name.
     *
     * @param name the building name
     */
    public void setBuildingName(String name) {
        this.buildingName = name;
    }

    /**
     * Gets the list of rooms.
     *
     * @return the list of rooms
     */
    public ArrayList<String> getRooms() {
        return rooms;
    }

    /**
     * Sets the list of rooms.
     *
     * @param rooms the list of rooms
     */
    public void setRooms(ArrayList<String> rooms) {
        this.rooms = rooms;
    }
}
