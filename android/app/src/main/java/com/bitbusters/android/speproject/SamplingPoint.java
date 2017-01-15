package com.bitbusters.android.speproject;

/**
 * Created by cp153 on 06/12/2016.
 */

class SamplingPoint {
    private String id;
    private double latitude;
    private double longitude;
    private String samplingPointType;

    SamplingPoint(String id, double latitude, double longitude, String samplingPointType) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.samplingPointType = samplingPointType;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getSamplingPointType() {
        return samplingPointType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setSamplingPointType(String samplingPointType) {
        this.samplingPointType = samplingPointType;
    }
}
