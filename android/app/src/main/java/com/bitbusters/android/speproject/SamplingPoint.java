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
}
