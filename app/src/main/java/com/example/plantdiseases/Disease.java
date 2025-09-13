package com.example.plantdiseases;

public class Disease {
    private String name;
    private String description;
    private String symptoms;

    public Disease() {}

    public Disease(String name, String description, String symptoms) {
        this.name = name;
        this.description = description;
        this.symptoms = symptoms;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSymptoms() { return symptoms; }
}
