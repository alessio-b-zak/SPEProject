package com.bitbusters.android.speproject;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cp153 on 06/12/2016.
 */

class CDEPoint extends Point {
    private String waterbodyId;
    private String type;
    private LatLng location;
    private Classification ecological;
    private Classification chemical;

    CDEPoint(String waterbodyId, String type, double latitude, double longitude) {
        super(latitude,longitude,"CDE_Point", "");
        this.waterbodyId = waterbodyId;
        this.type = type;
        this.location = new LatLng(latitude,longitude);
        this.ecological = new Classification("Ecological");
        this.chemical = new Classification("Chemical");
    }

    public String getWaterbodyId() {
        return waterbodyId;
    }

    public double getLatitude() {
        return location.latitude;
    }

    public double getLongitude() {
        return location.longitude;
    }

    public String getType() {
        return type;
    }

    public Classification getEcological() { return ecological; }

    public Classification getChemical() { return chemical; }

    public void setWaterbodyId(String waterbodyId) {
        this.waterbodyId = waterbodyId;
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

}
