package com.mobdeve.s18.banyoboyz.flushfinders.models;

public class BuildingData {
    private double latitude;
    private double longitude;
    private String name;
    private String address;
    private String buildingPicture;
    private RestroomData[] restroomData;

    public BuildingData(){}

    public BuildingData(double latitude, double longitude, String name, String address, String buildingPicture, RestroomData[] restroomData) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
        this.buildingPicture = buildingPicture;
        this.restroomData = restroomData;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBuildingPicture() {
        return buildingPicture;
    }

    public void setBuildingPicture(String buildingPicture) {
        this.buildingPicture = buildingPicture;
    }

    public RestroomData[] getRestroomData() {
        return restroomData;
    }

    public void setRestroomData(RestroomData[] restroomData) {
        this.restroomData = restroomData;
    }
}
