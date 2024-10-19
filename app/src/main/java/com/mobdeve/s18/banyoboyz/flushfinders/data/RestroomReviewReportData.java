package com.mobdeve.s18.banyoboyz.flushfinders.data;

public class RestroomReviewReportData {
    private int buildingImageResource;
    private float rating;
    private String reviewReport;
    private String buildingName;
    private String name;
    private RestroomMetrics metrics;

    public RestroomReviewReportData(int buildingImageResource, float rating, String reviewReport, String buildingName, String name, int cleanliness, int maintenance, int vacancy) {
        this.buildingImageResource = buildingImageResource;
        this.rating = rating;
        this.reviewReport = reviewReport;
        this.buildingName = buildingName;
        this.name = name;
        this.metrics = new RestroomMetrics(cleanliness, maintenance, vacancy);
    }

    public int getBuildingImageResource() {
        return buildingImageResource;
    }

    public void setBuildingImageResource(int buildingImageResource) {
        this.buildingImageResource = buildingImageResource;
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

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
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
}
