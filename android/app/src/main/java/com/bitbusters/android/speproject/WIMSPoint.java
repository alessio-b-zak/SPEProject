package com.bitbusters.android.speproject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cp153 on 06/12/2016.
 */

class WIMSPoint extends Point {
    private String id;
    private String type;
    private String label;
    private Integer easting;
    private Integer northing;
    private List<Measurement> measurementList;

    WIMSPoint(String id, double latitude, double longitude, String type,
              String label, Integer easting, Integer northing) {
        super(latitude,longitude,"WIMS_Point", "");
        this.id = id;
        this.type = type;
        this.label = label;
        this.easting = easting;
        this.northing = northing;
        this.measurementList = new ArrayList<>();
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

    public String getType() {
        return type;
    }

    public Integer getEasting() { return easting; }

    public Integer getNorthing() { return northing; }

    public List<Measurement> getMeasurementList() { return measurementList; }

    public String getLabel() { return label; }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        super.setLatitude(latitude);
    }

    public void setLongitude(double longitude) {
        super.setLongitude(longitude);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLabel(String label) { this.label = label; }

    public void setEasting(Integer easting) { this.easting = easting; }

    public void setNorthing(Integer northing) { this.northing = northing; }

    public boolean measurementsPopulated() {
        return measurementList.size() == 3;
    }
}
