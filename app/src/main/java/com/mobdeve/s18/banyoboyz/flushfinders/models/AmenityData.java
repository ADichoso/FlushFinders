package com.mobdeve.s18.banyoboyz.flushfinders.models;

public class AmenityData {
    private long id;
    private String name;
    private int amenityPictureResource;

    public AmenityData(){}


    public AmenityData(long id, String name, int amenityPictureResource) {
        this.id = id;
        this.name = name;
        this.amenityPictureResource = amenityPictureResource;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmenityPictureResource() {
        return amenityPictureResource;
    }

    public void setAmenityPictureResource(int amenityPictureResource) {
        this.amenityPictureResource = amenityPictureResource;
    }
}
