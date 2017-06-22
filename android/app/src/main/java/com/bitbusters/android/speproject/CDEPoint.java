package com.bitbusters.android.speproject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cp153 on 06/12/2016.
 */

class CDEPoint extends Point {
    private String waterbodyId;
    private String label;
    private LatLng location;
    private HashMap<String,Classification> classificationHashMap;

    public static final String ECOLOGICAL = "Ecological";
    public static final String CHEMICAL = "Chemical";
    public static final String OVERALL = "Overall Water Body";

    CDEPoint(String waterbodyId, String label, double latitude, double longitude) {
        super(latitude,longitude,"CDE_Point", "");
        this.waterbodyId = waterbodyId;
        this.label = label;
        this.location = new LatLng(latitude,longitude);
        this.classificationHashMap = new HashMap<>();
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

    public HashMap<String, Classification> getClassificationHashMap() { return classificationHashMap; }

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

    public boolean isClassified() {
        return classificationHashMap.size() == 3
                && classificationHashMap.containsKey(ECOLOGICAL)
                && classificationHashMap.containsKey(CHEMICAL)
                && classificationHashMap.containsKey(OVERALL);
    }

    public void printClassification() {
        for(Map.Entry<String, Classification> entry : classificationHashMap.entrySet()){
            Log.i("CDEPoint", entry.getKey() + " " + entry.getValue().getValue() + " "
                    + entry.getValue().getCertainty() + " " + entry.getValue().getYear());
        }
    }

}
