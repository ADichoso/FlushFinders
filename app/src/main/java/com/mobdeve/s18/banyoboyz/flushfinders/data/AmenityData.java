package com.mobdeve.s18.banyoboyz.flushfinders.data;

public class AmenityData {
    private String name;
    private int amenityPictureResource;

    public AmenityData(String name, int amenityPictureResource) {
        this.name = name;
        this.amenityPictureResource = amenityPictureResource;
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
