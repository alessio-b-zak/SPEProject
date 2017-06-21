package com.bitbusters.android.speproject;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cp153 on 06/12/2016.
 */

class CDEPoint extends Point {
    private String waterbodyId;
    private String label;
//    private String type;
    private LatLng location;
    private Classification ecological;
    private Classification chemical;

    CDEPoint(String waterbodyId, String label, double latitude, double longitude) {
        super(latitude,longitude,"CDE_Point", "");
        this.waterbodyId = waterbodyId;
        this.label = label;
//        this.type = type;
        this.location = new LatLng(latitude,longitude);
        this.ecological = new Classification("Ecological");
        this.chemical = new Classification("Chemical");
    }

    public String getWaterbodyId() {
        return waterbodyId;
    }

    public String getLabel() {
        return label;
    }

    public double getLatitude() {
        return location.latitude;
    }

    public double getLongitude() {
        return location.longitude;
    }

//    public String getType() {
//        return type;
//    }

    public Classification getEcological() { return ecological; }

    public Classification getChemical() { return chemical; }

    public void setWaterbodyId(String waterbodyId) {
        this.waterbodyId = waterbodyId;
    }

    public void setLabel(String label) { this.label = label; }

    public void setLatitude(double latitude) {
        super.setLatitude(latitude);
    }

    public void setLongitude(double longitude) {
        super.setLongitude(longitude);
    }

//    public void setType(String type) {
//        this.type = type;
//    }

}
