package com.mobdeve.s18.banyoboyz.flushfinders.models;

import com.mobdeve.s18.banyoboyz.flushfinders.models.adapters.RestroomMetrics;

public class RestroomData {
    private long id;
    private int buildingImageResource;
    private String buildingName;
    private String buildingAddress;
    private String name;
    private RestroomMetrics metrics;
    private AmenityData[] amenities;

    public RestroomData(){}

    public RestroomData(long id, int buildingImageResource, String buildingName, String buildingAddress, String name, int cleanliness, int maintenance, int vacancy, AmenityData[] amenities) {
        this.id = id;
        this.buildingImageResource = buildingImageResource;
        this.buildingName = buildingName;
        this.buildingAddress = buildingAddress;
        this.name = name;
        this.metrics = new RestroomMetrics(cleanliness, maintenance, vacancy);
        this.amenities = amenities;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBuildingImageResource() {
        return buildingImageResource;
    }

    public void setBuildingImageResource(int buildingImageResource) {
        this.buildingImageResource = buildingImageResource;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingAddress() {
        return buildingAddress;
    }

    public void setBuildingAddress(String buildingAddress) {
        this.buildingAddress = buildingAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RestroomMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(RestroomMetrics metrics) {
        this.metrics = metrics;
    }

    public AmenityData[] getAmenities() {
        return amenities;
    }

    public void setAmenities(AmenityData[] amenities) {
        this.amenities = amenities;
    }
}
