package com.guilherme_silva.countriesoftheworld.models;

public class Country {
    private String name;
    private String alpha2Code;
    private String subregion;
    private String capital;
    private float area;
    private int population;

    public String getName() {
        return name;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }

    public String getSubregion() {
        return subregion;
    }

    public String getCapital() {
        return capital;
    }

    public float getArea() {
        return area;
    }

    public int getPopulation() {
        return population;
    }
}
