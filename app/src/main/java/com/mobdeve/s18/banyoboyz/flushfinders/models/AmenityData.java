package com.mobdeve.s18.banyoboyz.flushfinders.models;

public class AmenityData {
    private String name;
    private String amenity_picture;

    public AmenityData(){}

    public AmenityData(String name, String amenity_picture) {
        this.name = name;
        this.amenity_picture = amenity_picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmenityPicture() {
        return amenity_picture;
    }

    public void setAmenityPicture(String amenity_picture) {
        this.amenity_picture = amenity_picture;
    }
}
