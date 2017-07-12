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

public class CDEPoint extends Point {
    private String waterbodyId;
    private String label;
    private LatLng location;
    private GeoJsonFeature geoJSONFeature;
    private HashMap<String, HashMap<String, Classification>> classificationHashMap;
    private List<RNAG> rnagList;

    private static final String TAG = "CDE_POINT";

    public static final String REAL = "Real";
    public static final String OBJECTIVE = "Objective";
    public static final String PREDICTED = "Predicted";

    // Classifications
    public static final String OVERALL = "Overall Water Body";

    public static final String ECOLOGICAL = "Ecological";
    public static final String SUPPORTING_ELEMENTS = "Supporting elements (Surface Water)";
    public static final String BIOLOGICAL_ELEMENTS = "Biological quality elements";
    public static final String HYDROMORPHOLOGICAL_ELEMENTS = "Hydromorphological Supporting Elements";
    public static final String PHYSICO_CHEMICAL_ELEMENTS = "Physico-chemical quality elements";
    public static final String SPECIFIC_POLLUTANTS = "Specific pollutants";

    public static final List<String> ecologicalGroup;
    static {
        ecologicalGroup = new ArrayList<>();
        ecologicalGroup.add(SUPPORTING_ELEMENTS);
        ecologicalGroup.add(BIOLOGICAL_ELEMENTS);
        ecologicalGroup.add(HYDROMORPHOLOGICAL_ELEMENTS);
        ecologicalGroup.add(PHYSICO_CHEMICAL_ELEMENTS);
        ecologicalGroup.add(SPECIFIC_POLLUTANTS);
    }

    public static final String CHEMICAL = "Chemical";
    public static final String PRIORITY_SUBSTANCES = "Priority substances";
    public static final String OTHER_POLLUTANTS = "Other Pollutants";
    public static final String HAZARDOUS_SUBSTANCES = " Priority hazardous substances";

    public static final List<String> chemicalGroup;
    static {
        chemicalGroup = new ArrayList<>();
        chemicalGroup.add(PRIORITY_SUBSTANCES);
        chemicalGroup.add(OTHER_POLLUTANTS);
        chemicalGroup.add(HAZARDOUS_SUBSTANCES);
    }

    // Ratings
    public static final String FAIL = "Fail";
    public static final String BAD = "Bad";
    public static final String POOR = "Poor";
    public static final String GOOD = "Good";
    public static final String HIGH = "High";
    public static final String MODERATE = "Moderate";
    public static final String NOT_REQUIRED = "Does not require assessment";
    public static final String SUPPORTS_GOOD = "Supports Good";
    public static final String DOES_NOT_SUPPORTS_GOOD = "Does Not Supports Good";
    public static final String NO_TREND = "No trend";
    public static final String UPWARD_TREND = "Upward trend";
    public static final String REVERSAL_OF_TREND = "Reversal of trend";
    public static final String NOT_ASSESSED = "Not assessed";
    public static final String ACTIVE = "Active";
    public static final String FOR_INFORMATION = "For information";
    public static final String FAILS_THRESHOLD = "Fails Threshold";
    public static final String PASSES_THRESHOLD = "Passes Threshold";


    public static final HashMap<String, String> classificationPrintValues;
    static {
        classificationPrintValues = new HashMap<>();
        classificationPrintValues.put(OVERALL, "Overall");
        classificationPrintValues.put(ECOLOGICAL, "Ecological");
        classificationPrintValues.put(CHEMICAL, "Chemical");
        classificationPrintValues.put(SUPPORTING_ELEMENTS, "Supporting");
        classificationPrintValues.put(BIOLOGICAL_ELEMENTS, "Biological");
        classificationPrintValues.put(HYDROMORPHOLOGICAL_ELEMENTS, "Hydromorphological");
        classificationPrintValues.put(PHYSICO_CHEMICAL_ELEMENTS, "Physico-chemical");
        classificationPrintValues.put(SPECIFIC_POLLUTANTS, "Specific");
        classificationPrintValues.put(PRIORITY_SUBSTANCES, "Priority");
        classificationPrintValues.put(OTHER_POLLUTANTS, "Other");
        classificationPrintValues.put(HAZARDOUS_SUBSTANCES, "Hazardous");
    }

    public static final HashMap<String, String> ratingPrintValues;
    static {
        ratingPrintValues = new HashMap<>();
        ratingPrintValues.put(POOR, "Poor");
        ratingPrintValues.put(GOOD, "Good");
        ratingPrintValues.put(HIGH, "High");
        ratingPrintValues.put(MODERATE, "Moderate");
        ratingPrintValues.put(FAIL, "Fail");
        ratingPrintValues.put(NOT_REQUIRED, "Not Required");
        ratingPrintValues.put(BAD, "Bad");
        ratingPrintValues.put(ACTIVE, "Active");
        ratingPrintValues.put(SUPPORTS_GOOD, "Aids Good");
        ratingPrintValues.put(DOES_NOT_SUPPORTS_GOOD, "Not Aiding Good");
        ratingPrintValues.put(NO_TREND, "No Trend");
        ratingPrintValues.put(UPWARD_TREND, "Up Trend");
        ratingPrintValues.put(REVERSAL_OF_TREND, "Down Trend");
        ratingPrintValues.put(NOT_ASSESSED, "No Result");
        ratingPrintValues.put(FOR_INFORMATION, "For Info");
        ratingPrintValues.put(FAILS_THRESHOLD, "Fails");
        ratingPrintValues.put(PASSES_THRESHOLD, "Passes");
    }

    CDEPoint(String waterbodyId, String label, double latitude, double longitude, GeoJsonFeature geoJSONFeature) {
        super(latitude,longitude,"CDE_Point", "");
        this.waterbodyId = waterbodyId;
        this.label = label;
        this.location = new LatLng(latitude,longitude);
        this.classificationHashMap = new HashMap<String, HashMap<String, Classification>>();
        this.classificationHashMap.put(REAL, new HashMap<String, Classification>());
        this.classificationHashMap.put(OBJECTIVE, new HashMap<String, Classification>());
        this.classificationHashMap.put(PREDICTED, new HashMap<String, Classification>());
        this.geoJSONFeature = geoJSONFeature;
        this.rnagList = new ArrayList<>();
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

    public HashMap<String, Classification> getClassificationHashMap(String group) {
        return classificationHashMap.get(group);
    }

    public List<RNAG> getRnagList() {
        return rnagList;
    }

    public void addRNAG(RNAG rnag) {
        rnagList.add(rnag);
    }

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

    public void printClassification(String group) {
        Log.i(TAG, group);
        for(Map.Entry<String, Classification> entry : classificationHashMap.get(group).entrySet()){
            Log.i(TAG, entry.getKey() + " " + entry.getValue().getValue() + " "
                    + entry.getValue().getCertainty() + " " + entry.getValue().getYear());
        }
    }

}
