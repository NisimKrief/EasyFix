package com.example.easyfix.Classes;

import java.util.ArrayList;

public class Building {
    private String buildingName;
    private ArrayList<String> rooms; // List of rooms in the building

    public Building() {
    }

    public Building(String name, ArrayList<String> rooms) {
        this.buildingName = name;
        this.rooms = rooms;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String name) {
        this.buildingName = name;
    }

    public ArrayList<String> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<String> rooms) {
        this.rooms = rooms;
    }
}
