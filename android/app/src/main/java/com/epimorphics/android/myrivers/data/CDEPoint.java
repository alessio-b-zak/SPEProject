package com.epimorphics.android.myrivers.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.geojson.GeoJsonFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Data class representing an individual waterbody found in Catchment Data Explorer:
 * @see <a href="http://environment.data.gov.uk/catchment-planning/">Catchment Data Explorer</a>
 *
 */
public class CDEPoint {
    /**
     * Constants used for manipulating data
     */
    public static final String RNAG = "RNAG";
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
    public static final String CHEMICAL = "Chemical";
    public static final String PRIORITY_SUBSTANCES = "Priority substances";
    public static final String OTHER_POLLUTANTS = "Other Pollutants";
    public static final String HAZARDOUS_SUBSTANCES = " Priority hazardous substances";

    public static final List<String> ecologicalGroup;
    static {
        ecologicalGroup = new ArrayList<>();
        ecologicalGroup.add(SUPPORTING_ELEMENTS);
        ecologicalGroup.add(BIOLOGICAL_ELEMENTS);
        ecologicalGroup.add(HYDROMORPHOLOGICAL_ELEMENTS);
        ecologicalGroup.add(PHYSICO_CHEMICAL_ELEMENTS);
        ecologicalGroup.add(SPECIFIC_POLLUTANTS);
    }

    public static final List<String> chemicalGroup;
    static {
        chemicalGroup = new ArrayList<>();
        chemicalGroup.add(PRIORITY_SUBSTANCES);
        chemicalGroup.add(OTHER_POLLUTANTS);
        chemicalGroup.add(HAZARDOUS_SUBSTANCES);
    }

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
        ratingPrintValues.put("N/A", "N/A");
    }

    private String waterbodyId;
    private String label;
    private LatLng location;
    private GeoJsonFeature riverPolygon;
    private GeoJsonFeature riverLine;
    private HashMap<String, HashMap<String, Classification>> classificationHashMap;
    private List<RNAG> rnagList;

    /**
     * Data constructor for a CDE waterbody
     *
     * @param waterbodyId identifier
     * @param label name
     * @param latitude latitude
     * @param longitude longitude
     * @param riverPolygon river catchment as GeoJsonFeature
     */
    public CDEPoint(String waterbodyId, String label, double latitude, double longitude, GeoJsonFeature riverPolygon) {
        this.waterbodyId = waterbodyId;
        this.label = label;
        this.location = new LatLng(latitude, longitude);
        this.classificationHashMap = new HashMap<String, HashMap<String, Classification>>();
        this.classificationHashMap.put(REAL, new HashMap<String, Classification>());
        this.classificationHashMap.put(OBJECTIVE, new HashMap<String, Classification>());
        this.classificationHashMap.put(PREDICTED, new HashMap<String, Classification>());
        this.riverPolygon = riverPolygon;
        this.riverLine = null;
        this.rnagList = new ArrayList<>();
    }

    /**
     * @return String waterbodyId
     */
    public String getWaterbodyId() {
        return waterbodyId;
    }

    /**
     * @return String label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return double latitude
     */
    public double getLatitude() {
        return location.latitude;
    }

    /**
     * @return double longitude
     */
    public double getLongitude() {
        return location.longitude;
    }

    /**
     * @return GeoJsonFeature riverPolygon(i.e. river catchment)
     */
    public GeoJsonFeature getRiverPolygon() {
        return riverPolygon;
    }

    /**
     * @return GeoJsonFeature riverLine
     */
    public GeoJsonFeature getRiverLine() {
        return riverLine;
    }

    /**
     * @param group for which a HashMap of classifications is to be returned
     * @return HashMap group of a given group
     * @see Classification
     */
    public HashMap<String, Classification> getClassificationHashMap(String group) {
        return classificationHashMap.get(group);
    }

    /**
     * @return List\<RNAG\> rnagList
     * @see RNAG
     */
    public List<RNAG> getRnagList() {
        return rnagList;
    }

    /**
     * @param rnag a single reason for not achieving good to be added to the list
     * @see RNAG
     */
    public void addRNAG(RNAG rnag) {
        rnagList.add(rnag);
    }

    /**
     * @param waterbodyId waterbody identifier
     */
    public void setWaterbodyId(String waterbodyId) {
        this.waterbodyId = waterbodyId;
    }

    /**
     * @param label waterbody name
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @param riverLine GeoJsonFeature representing river line
     * @see GeoJsonFeature
     */
    public void setRiverLine(GeoJsonFeature riverLine) {
        this.riverLine = riverLine;
    }

}
