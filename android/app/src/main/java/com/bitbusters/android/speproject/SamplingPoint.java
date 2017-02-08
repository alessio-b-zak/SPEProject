package com.bitbusters.android.speproject;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by cp153 on 06/12/2016.
 */

class SamplingPoint extends Point {
    private String id;
    private String samplingPointType;

    SamplingPoint(String id, double latitude, double longitude, String samplingPointType) {
        super(latitude,longitude,"Sample_Point", "");
        this.id = id;
        this.samplingPointType = samplingPointType;

    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return this.getPosition().latitude;
    }

    public double getLongitude() {
        return this.getPosition().longitude;
    }

    public String getSamplingPointType() {
        return samplingPointType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        super.setLatitude(latitude);
    }

    public void setLongitude(double longitude) {
        super.setLongitude(longitude);
    }

    public void setSamplingPointType(String samplingPointType) {
        this.samplingPointType = samplingPointType;
    }


}
