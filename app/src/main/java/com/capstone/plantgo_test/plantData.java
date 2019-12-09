package com.capstone.plantgo_test;

import java.util.HashMap;
import java.util.Map;

public class plantData {
    public String name;
    public String location;
    public String temperature;
    public String watertiming;
    public String type;
    public String image;

    public plantData() {

    }

    public plantData(String name, String loca, String temp, String water, String type, String image) {
        this.name = name;
        this.location = loca;
        this.temperature = temp;
        this.watertiming = water;
        this.type = type;
        this.image = image;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("location", location);
        result.put("temperature", temperature);
        result.put("watertiming", watertiming);
        result.put("type", type);
        result.put("image", image);

        return result;
    }
}
