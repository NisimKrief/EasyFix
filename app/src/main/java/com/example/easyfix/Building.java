package com.example.easyfix;

import java.util.ArrayList;

public class Building {
    ArrayList<String> Area; // אזורים אפשריים לדיווח

    public Building(ArrayList<String> area) {
        Area = area;
    }

    public ArrayList<String> getArea() {
        return Area;
    }

    public void setArea(ArrayList<String> area) {
        Area = area;
    }
}

//שם המתחם - מסוג  String
