package com.bitbusters.android.speproject;

/**
 * Created by cp153 on 06/12/2016.
 */

class SamplingPoint {
    public String id;
    public double latitude;
    public double longitude;
    public String samplingPointType;

    SamplingPoint(String id, double latitude, double longitude, String samplingPointType) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.samplingPointType = samplingPointType;
    }

//TODO: Tudor should write getters and setters
}
