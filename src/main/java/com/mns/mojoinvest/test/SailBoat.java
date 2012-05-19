package com.mns.mojoinvest.test;

public class SailBoat {
    private String name;
    private int lengthOverAll;

    SailBoat(String name, int lengthOverAll) {
        this.name = name;
        this.lengthOverAll = lengthOverAll;
    }

    public String getName() {
        return name;
    }

    public int getLengthOverAll() {
        return lengthOverAll;
    }
    // Setters ...
}