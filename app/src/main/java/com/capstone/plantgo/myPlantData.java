package com.capstone.plantgo;

public class myPlantData {
    public String name;
    public String nickName;
    public String state;
    public String image;
    public String addDate;
    public String Temperature;
    public String Soil;
    public String LightTop;
    public String LightRight;
    public String LightLeft;
    public String Humid;
    public String Dust;

    public myPlantData() {
    }

    public myPlantData(String name, String nickName, String state, String image, String addDate, String Temperature, String Soil, String LightTop, String LightRight, String LightLeft, String Humid, String Dust) {
        this.name = name;
        this.nickName = nickName;
        this.state = state;
        this.image = image;
        this.addDate = addDate;
        this.Temperature = Temperature;
        this.Soil = Soil;
        this.LightTop = LightTop;
        this.LightRight = LightRight;
        this.LightLeft = LightLeft;
        this.Humid = Humid;
        this.Dust = Dust;
    }
}
