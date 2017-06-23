package com.bitbusters.android.speproject;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by cp153 on 06/12/2016.
 */

class WIMSPoint extends Point {
    private String id;
    private String samplingPointType;
    private Integer easting;
    private Integer northing;
    private String ecologicalRating;
    private String chemicalRating;
    private Boolean ratingsSet;

    WIMSPoint(String id, double latitude, double longitude, String samplingPointType,
              Integer easting, Integer northing) {
        super(latitude,longitude,"WIMS_Point", "");
        this.id = id;
        this.samplingPointType = samplingPointType;
        this.easting = easting;
        this.northing = northing;
        this.ecologicalRating = "N/A";
        this.chemicalRating = "N/A";
        this.ratingsSet = false;
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

    public Integer getEasting() { return easting; }

    public Integer getNorthing() { return northing; }

    public String getEcologicalRating() { return ecologicalRating; }

    public String getChemicalRating() { return chemicalRating; }

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

    public void setEasting(Integer easting) { this.easting = easting; }

    public void setNorthing(Integer northing) { this.northing = northing; }

    public void setEcologicalRating(String ecologicalRating) {
        this.ecologicalRating = ecologicalRating;
        this.ratingsSet = true;
    }

    public void setChemicalRating(String chemicalRating) { this.chemicalRating = chemicalRating; }

    public Boolean getRatingsSet() {
        return ratingsSet;
    }
}
