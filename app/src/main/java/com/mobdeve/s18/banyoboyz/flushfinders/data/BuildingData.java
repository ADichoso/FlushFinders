package com.mobdeve.s18.banyoboyz.flushfinders.data;

public class BuildingData {
    private String name;
    private String address;
    private String estimatedWalkingTime;
    private int buildingPictureResource;
    private RestroomData[] restroomData;

    public BuildingData(String name, String address, String estimatedWalkingTime, int buildingPictureResource, RestroomData[] restroomData) {
        this.name = name;
        this.address = address;
        this.estimatedWalkingTime = estimatedWalkingTime;
        this.buildingPictureResource = buildingPictureResource;
        this.restroomData = restroomData;
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

    public String getEstimatedWalkingTime() {
        return estimatedWalkingTime;
    }

    public void setEstimatedWalkingTime(String estimatedWalkingTime) {
        this.estimatedWalkingTime = estimatedWalkingTime;
    }

    public int getBuildingPictureResource() {
        return buildingPictureResource;
    }

    public void setBuildingPictureResource(int buildingPictureResource) {
        this.buildingPictureResource = buildingPictureResource;
    }

    public RestroomData[] getRestroomData() {
        return restroomData;
    }

    public void setRestroomData(RestroomData[] restroomData) {
        this.restroomData = restroomData;
    }
}
