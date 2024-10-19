package com.mobdeve.s18.banyoboyz.flushfinders.data;

public class RestroomMetrics
{
    private int cleanliness;
    private int maintenance;
    private int vacancy;

    public RestroomMetrics(int cleanliness, int maintenance, int vacancy) {
        this.cleanliness = cleanliness;
        this.maintenance = maintenance;
        this.vacancy = vacancy;
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
