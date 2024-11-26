package com.mobdeve.s18.banyoboyz.flushfinders.models;

import java.util.ArrayList;
import java.util.Comparator;

public class RestroomData implements Comparator<RestroomData>
{
    private String id;
    private String building_id;
    private String building_picture;
    private String building_name;
    private String building_address;
    private String name;
    private int cleanliness;
    private int maintenance;
    private int vacancy;
    private ArrayList<AmenityData> amenities;

    public RestroomData(String id,
                        String building_id,
                        String building_picture,
                        String building_name,
                        String building_address,
                        String name,
                        int cleanliness,
                        int maintenance,
                        int vacancy,
                        ArrayList<AmenityData> amenities)
    {
        this.id = id;
        this.building_id = building_id;
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

    public String getBuildingId() {
        return building_id;
    }

    public void setBuildingId(String building_id) {
        this.building_id = building_id;
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

    public ArrayList<AmenityData> getAmenities() {
        return amenities;
    }


    public void setAmenities(ArrayList<AmenityData> amenities) {
        this.amenities = amenities;
    }

    @Override
    public int compare(RestroomData r1, RestroomData r2) {
        // First compare based on cleanliness
        if (r1.getCleanliness() != r2.getCleanliness()) {
            return Integer.compare(r2.getCleanliness(), r1.getCleanliness());  // Higher cleanliness first
        }

        // If cleanliness is the same, compare based on maintenance
        if (r1.getMaintenance() != r2.getMaintenance()) {
            return Integer.compare(r2.getMaintenance(), r1.getMaintenance());  // Higher maintenance first
        }

        // If both cleanliness and maintenance are the same, compare based on vacancy
        return Integer.compare(r2.getVacancy(), r1.getVacancy());  // Higher vacancy first
    }
}
