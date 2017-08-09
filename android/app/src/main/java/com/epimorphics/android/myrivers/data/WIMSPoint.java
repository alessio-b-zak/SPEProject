package com.epimorphics.android.myrivers.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data class storing water quality sample details found in Water Quality Data Archive(WIMS).
 * Extends Point.
 *
 * @see <a href="http://environment.data.gov.uk/water-quality/view/landing">Water Quality Data Archive</a>
 * @see Point
 */
public class WIMSPoint extends Point {
    // Determinands that are relevant for the application
    private static final String TEMPERATURE = "Temp Water";
    private static final String CONDUCTIVITY = "Cond @ 25C";
    private static final String PH = "pH";
    private static final String DISS_OXYGEN_O2 = "Oxygen Diss";
    private static final String DISS_OXYGEN_PERCENT = "O Diss %sat";
    private static final String COD = "COD as O2";
    private static final String BOD = "BOD ATU";
    private static final String NITRATE = "Nitrate-N";
    private static final String NITRATE_FILTERED = "Nitrate Filt";
    private static final String PHOSPHATE = "Phosphate";
    private static final String ORTHOPHOSPHATE_REACTIVE = "Orthophospht";
    private static final String ORTHOPHOSPHATE_FILTERED = "OrthophsFilt";
    private static final String SOLIDS_105C_SUSPEND = "Sld Sus@105C";
    private static final String SOLIDS_105C_DISSOLVE = "Sld Filt@105";
    private static final String SOLIDS_500C_VOLATILE = "Sld V @ 500C";
    private static final String SOLIDS_500C_NONVOLATILE = "Sld NV@500C";

    public static final ArrayList<String> generalGroup;
    static {
        generalGroup = new ArrayList<>();
        generalGroup.add(TEMPERATURE);
        generalGroup.add(CONDUCTIVITY);
        generalGroup.add(PH);
    }

    public static final ArrayList<String> dissolvedOxygenGroup;
    static {
        dissolvedOxygenGroup = new ArrayList<>();
        dissolvedOxygenGroup.add(DISS_OXYGEN_O2);
        dissolvedOxygenGroup.add(DISS_OXYGEN_PERCENT);
    }

    public static final ArrayList<String> oxygenDemandGroup;
    static {
        oxygenDemandGroup = new ArrayList<>();
        oxygenDemandGroup.add(COD);
        oxygenDemandGroup.add(BOD);
    }

    public static final ArrayList<String> nitrateGroup;
    static {
        nitrateGroup = new ArrayList<>();
        nitrateGroup.add(NITRATE);
        nitrateGroup.add(NITRATE_FILTERED);
    }

    public static final ArrayList<String> phosphateGroup;
    static {
        phosphateGroup = new ArrayList<>();
        phosphateGroup.add(PHOSPHATE);
        phosphateGroup.add(ORTHOPHOSPHATE_FILTERED);
        phosphateGroup.add(ORTHOPHOSPHATE_REACTIVE);
    }

    public static final ArrayList<String> solidGroup;
    static {
        solidGroup = new ArrayList<>();
        solidGroup.add(SOLIDS_105C_DISSOLVE);
        solidGroup.add(SOLIDS_105C_SUSPEND);
        solidGroup.add(SOLIDS_500C_VOLATILE);
        solidGroup.add(SOLIDS_500C_NONVOLATILE);
    }

    public static final ArrayList<String> nonMetalGroup;
    static {
        nonMetalGroup = new ArrayList<>();
        nonMetalGroup.addAll(solidGroup);
        nonMetalGroup.addAll(phosphateGroup);
        nonMetalGroup.addAll(nitrateGroup);
        nonMetalGroup.addAll(oxygenDemandGroup);
        nonMetalGroup.addAll(dissolvedOxygenGroup);
        nonMetalGroup.addAll(generalGroup);
    }

    private String id;
    private String label;
    private Double distance;
    private HashMap<String, ArrayList<Measurement>> measurementMap;

    /**
     * Data constructor for a WIMS sampling point
     *
     * @param id sampling point identifier
     * @param latitude latitude
     * @param longitude longitude
     */
    public WIMSPoint(String id, double latitude, double longitude) {
        super(latitude, longitude, "WIMS_Point", "");
        this.id = id;
        this.label = null;
        this.distance = 0.0;
        this.measurementMap = new HashMap<>();
    }

    /**
     * Data constructor for a WIMS sampling point including distance which is used inside MyArea
     *
     * @param id sampling point identifier
     * @param latitude latitude
     * @param longitude longitude
     * @param distance distance to the user's location
     *
     * @see MyArea
     */
    public WIMSPoint(String id, double latitude, double longitude, double distance) {
        super(latitude, longitude, "WIMS_Point", "");
        this.id = id;
        this.label = null;
        this.distance = distance;
        this.measurementMap = new HashMap<>();
    }

    /**
     * @return String id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id sampling point identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return double latitude
     */
    public double getLatitude() {
        return this.getPosition().latitude;
    }

    /**
     * @param latitude latitude of a point
     */
    public void setLatitude(double latitude) {
        super.setLatitude(latitude);
    }

    /**
     * @return double longitude
     */
    public double getLongitude() {
        return this.getPosition().longitude;
    }

    /**
     * @param longitude longitude of a point
     */
    public void setLongitude(double longitude) {
        super.setLongitude(longitude);
    }

    /**
     * @return double distance to the user
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @return HashMap measurementMap
     */
    public HashMap<String, ArrayList<Measurement>> getMeasurementMap() {
        return measurementMap;
    }

    /**
     * @return String label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label sampling point label
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
