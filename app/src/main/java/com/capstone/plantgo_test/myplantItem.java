package com.capstone.plantgo_test;

public class myplantItem {

    String lightLeft;
    String lightTop;
    String lightRight;
    String soilMoisture;
    String temperature;
    String humid;
    String image;
    String name;
    String state;
    String nickname;

    myplantItem() {
    }

    myplantItem(String humid, String lightLeft, String lightRight, String lightTop, String soilMoisture, String temperature, String image, String name, String state, String nickname) {
        this.humid = humid;
        this.lightLeft = lightLeft;
        this.lightRight = lightRight;
        this.lightTop = lightTop;
        this.soilMoisture = soilMoisture;
        this.temperature = temperature;
        this.image = image;
        this.name = name;
        this.state = state;
        this.nickname = nickname;
    }

    public String getHumid() {
        return humid;
    }

    public String getLightLeft() {
        return lightLeft;
    }

    public String getLightRight() {
        return lightRight;
    }

    public String getLightTop() {
        return lightTop;
    }

    public String getSoilMoisture() {
        return soilMoisture;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getnickName() {
        return nickname;
    }

    public void setHumid(String humid) {
        this.humid = humid;
    }

    public void setLightLegt(String lightLeft) {
        this.lightLeft = lightLeft;
    }

    public void setLightRight(String lightRight) {
        this.lightRight = lightRight;
    }

    public void setLightTop(String lightTop) {
        this.lightTop = lightTop;
    }

    public void setSoilMoisture(String soilMoisture) {
        this.soilMoisture = soilMoisture;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setnickName(String nickname) {
        this.nickname = nickname;
    }
}
