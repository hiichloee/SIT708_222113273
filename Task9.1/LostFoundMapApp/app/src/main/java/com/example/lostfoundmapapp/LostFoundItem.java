package com.example.lostfoundmapapp;

import java.io.Serializable;

public class LostFoundItem implements Serializable {
    private int id;
    private String type, name, phone, description, date, location;
    private double latitude, longitude;

    public LostFoundItem(int id, String type, String name, String phone,
                         String description, String date, String location,
                         double latitude, double longitude) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.date = date;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}