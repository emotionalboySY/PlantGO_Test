package com.capstone.plantgo_test;

public class pbookItem {

    String image;
    String location;
    String name;
    String temperature;
    String type;
    String watertiming;

    pbookItem() {}

    pbookItem(String image, String location, String name, String temperature, String type, String watertiming) {
        this.image = image;
        this.location = location;
        this.name = name;
        this.temperature = temperature;
        this.type = type;
        this.watertiming = watertiming;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getImage() {
        return this.image;
    }

    public String getLocation() {
        return location;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWatertiming() {
        return watertiming;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setWatertiming(String watertiming) {
        this.watertiming = watertiming;
    }
}
