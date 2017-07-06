package com.bitbusters.android.speproject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonFeature;

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
    private GeoJsonFeature geoJSONFeature;
    private HashMap<String,Classification> classificationHashMap;

    public static final String OVERALL = "Overall Water Body";

    public static final String ECOLOGICAL = "Ecological";
    public static final String SUPPORTING_ELEMENTS = "Supporting elements (Surface Water)";
    public static final String BIOLOGICAL_ELEMENTS = "Biological quality elements";
    public static final String HYDROMORPHOLOGICAL_ELEMENTS = "Hydromorphological Supporting Elements";
    public static final String PHYSICO_CHEMICAL_ELEMENTS = "Physico-chemical quality elements";
    public static final String SPECIFIC_POLLUTANTS = "Specific pollutants";

    public static final String CHEMICAL = "Chemical";
    public static final String PRIORITY_SUBSTANCES = "Priority substances";
    public static final String OTHER_POLLUTANTS = "Other Pollutants";
    public static final String HAZARDOUS_SUBSTANCES = " Priority hazardous substances";

    public static final String POOR = "Poor";
    public static final String GOOD = "Good";
    public static final String MODERATE = "Moderate";

    CDEPoint(String waterbodyId, String label, double latitude, double longitude, GeoJsonFeature geoJSONFeature) {
        super(latitude,longitude,"CDE_Point", "");
        this.waterbodyId = waterbodyId;
        this.label = label;
        this.location = new LatLng(latitude,longitude);
        this.classificationHashMap = new HashMap<>();
        this.geoJSONFeature = geoJSONFeature;
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

    public GeoJsonFeature getGeoJSONFeature() {
        return geoJSONFeature;
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

    public void printClassification() {
        for(Map.Entry<String, Classification> entry : classificationHashMap.entrySet()){
            Log.i("CDEPoint", entry.getKey() + " " + entry.getValue().getValue() + " "
                    + entry.getValue().getCertainty() + " " + entry.getValue().getYear());
        }
    }

}
