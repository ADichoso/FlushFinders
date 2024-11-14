package com.mobdeve.s18.banyoboyz.flushfinders.models;

public class RestroomData {
    private String id;
    private String building_picture;
    private String building_name;
    private String building_address;
    private String name;
    private int cleanliness;
    private int maintenance;
    private int vacancy;
    private AmenityData[] amenities;

    public RestroomData(){}

    public RestroomData(String id, String building_picture, String building_name, String building_address, String name, int cleanliness, int maintenance, int vacancy, AmenityData[] amenities) {
        this.id = id;
        this.building_picture = building_picture;
        this.building_name = building_name;
        this.building_address = building_address;
        this.name = name;
        this.cleanliness = cleanliness;
        this.maintenance = maintenance;
        this.vacancy = vacancy;
        this.amenities = amenities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuildingPicture() {
        return building_picture;
    }

    public void setBuildingPicture(String building_picture) {
        this.building_picture = building_picture;
    }

    public String getBuildingName() {
        return building_name;
    }

    public void setBuildingName(String building_name) {
        this.building_name = building_name;
    }

    public String getBuildingAddress() {
        return building_address;
    }

    public void setBuildingAddress(String building_address) {
        this.building_address = building_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCleanliness() {
        return cleanliness;
    }

    public void setCleanliness(int cleanliness) {
        this.cleanliness = cleanliness;
    }

    public int getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(int maintenance) {
        this.maintenance = maintenance;
    }

    public int getVacancy() {
        return vacancy;
    }

    public void setVacancy(int vacancy) {
        this.vacancy = vacancy;
    }

    public AmenityData[] getAmenities() {
        return amenities;
    }

    public void setAmenities(AmenityData[] amenities) {
        this.amenities = amenities;
    }
}
