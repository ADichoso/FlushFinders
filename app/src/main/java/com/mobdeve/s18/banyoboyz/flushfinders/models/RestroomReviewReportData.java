package com.mobdeve.s18.banyoboyz.flushfinders.models;

public class RestroomReviewReportData {
    private String id;

    //Building Info
    private String building_name;
    private String building_address;
    private String building_picture;

    //Restroom Info
    private String restroom_id;


    //Account Info
    private String reviewer_email;

    //Rating Info
    private float rating;
    private String reviewReport;
    private int cleanliness;
    private int maintenance;
    private int vacancy;

    public RestroomReviewReportData(String id, String building_name, String building_address, String building_picture, String restroom_id, String reviewer_email, float rating, String reviewReport, int cleanliness, int maintenance, int vacancy) {
        this.id = id;
        this.building_name = building_name;
        this.building_address = building_address;
        this.building_picture = building_picture;
        this.restroom_id = restroom_id;
        this.reviewer_email = reviewer_email;
        this.rating = rating;
        this.reviewReport = reviewReport;
        this.cleanliness = cleanliness;
        this.maintenance = maintenance;
        this.vacancy = vacancy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBuildingPicture() {
        return building_picture;
    }

    public void setBuildingPicture(String building_picture) {
        this.building_picture = building_picture;
    }

    public String getRestroomId() {
        return restroom_id;
    }

    public void setRestroomId(String restroom_id) {
        this.restroom_id = restroom_id;
    }

    public String getReviewerEmail() {
        return reviewer_email;
    }

    public void setReviewerEmail(String reviewer_email) {
        this.reviewer_email = reviewer_email;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReviewReport() {
        return reviewReport;
    }

    public void setReviewReport(String reviewReport) {
        this.reviewReport = reviewReport;
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
}
