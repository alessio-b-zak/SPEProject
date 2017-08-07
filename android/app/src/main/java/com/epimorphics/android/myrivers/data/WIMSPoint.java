package com.epimorphics.android.myrivers.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cp153 on 06/12/2016.
 */

public class WIMSPoint extends Point {
    public static final String TEMPERATURE = "Temp Water";
    public static final String CONDUCTIVITY = "Cond @ 25C";
    public static final String PH = "pH";
    public static final String DISS_OXYGEN_O2 = "Oxygen Diss";
    public static final String DISS_OXYGEN_PERCENT = "O Diss %sat";
    public static final String COD = "COD as O2";
    public static final String BOD = "BOD ATU";
    public static final String NITRATE = "Nitrate-N";
    public static final String NITRATE_FILTERED = "Nitrate Filt";
    public static final String PHOSPHATE = "Phosphate";
    public static final String ORTHOPHOSPHATE_REACTIVE = "Orthophospht";
    public static final String ORTHOPHOSPHATE_FILTERED = "OrthophsFilt";
    public static final String SOLIDS_105C_SUSPEND = "Sld Sus@105C";
    public static final String SOLIDS_105C_DISSOLVE = "Sld Filt@105";
    public static final String SOLIDS_500C_VOLATILE = "Sld V @ 500C";
    public static final String SOLIDS_500C_NONVOLATILE = "Sld NV@500C";
    public static final ArrayList<String> generalGroup;
    public static final ArrayList<String> dissolvedOxygenGroup;
    public static final ArrayList<String> oxygenDemandGroup;
    public static final ArrayList<String> nitrateGroup;
    public static final ArrayList<String> phosphateGroup;
    public static final ArrayList<String> solidGroup;
    public static final ArrayList<String> nonMetalGroup;

    static {
        generalGroup = new ArrayList<>();
        generalGroup.add(TEMPERATURE);
        generalGroup.add(CONDUCTIVITY);
        generalGroup.add(PH);
    }

    static {
        dissolvedOxygenGroup = new ArrayList<>();
        dissolvedOxygenGroup.add(DISS_OXYGEN_O2);
        dissolvedOxygenGroup.add(DISS_OXYGEN_PERCENT);
    }

    static {
        oxygenDemandGroup = new ArrayList<>();
        oxygenDemandGroup.add(COD);
        oxygenDemandGroup.add(BOD);
    }

    static {
        nitrateGroup = new ArrayList<>();
        nitrateGroup.add(NITRATE);
        nitrateGroup.add(NITRATE_FILTERED);
    }

    static {
        phosphateGroup = new ArrayList<>();
        phosphateGroup.add(PHOSPHATE);
        phosphateGroup.add(ORTHOPHOSPHATE_FILTERED);
        phosphateGroup.add(ORTHOPHOSPHATE_REACTIVE);
    }

    static {
        solidGroup = new ArrayList<>();
        solidGroup.add(SOLIDS_105C_DISSOLVE);
        solidGroup.add(SOLIDS_105C_SUSPEND);
        solidGroup.add(SOLIDS_500C_VOLATILE);
        solidGroup.add(SOLIDS_500C_NONVOLATILE);
    }

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
    private String type;
    private String label;
    private Integer easting;
    private Integer northing;
    private Double distance;
    private HashMap<String, ArrayList<Measurement>> measurementMap;


    public WIMSPoint(String id, double latitude, double longitude, String type,
              String label, Integer easting, Integer northing) {
        super(latitude, longitude, "WIMS_Point", "");
        this.id = id;
        this.type = type;
        this.label = label;
        this.easting = easting;
        this.northing = northing;
        this.distance = 0.0;
        this.measurementMap = new HashMap<>();
    }

    public WIMSPoint(String id, double latitude, double longitude) {
        super(latitude, longitude, "WIMS_Point", "");
        this.id = id;
        this.type = null;
        this.label = null;
        this.easting = null;
        this.northing = null;
        this.distance = 0.0;
        this.measurementMap = new HashMap<String, ArrayList<Measurement>>();
    }

    public WIMSPoint(String id, double latitude, double longitude, double distance) {
        super(latitude, longitude, "WIMS_Point", "");
        this.id = id;
        this.type = null;
        this.label = null;
        this.easting = null;
        this.northing = null;
        this.distance = distance;
        this.measurementMap = new HashMap<String, ArrayList<Measurement>>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return this.getPosition().latitude;
    }

    public void setLatitude(double latitude) {
        super.setLatitude(latitude);
    }

    public double getLongitude() {
        return this.getPosition().longitude;
    }

    public void setLongitude(double longitude) {
        super.setLongitude(longitude);
    }

    public double getDistance() {
        return distance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getEasting() {
        return easting;
    }

    public void setEasting(Integer easting) {
        this.easting = easting;
    }

    public Integer getNorthing() {
        return northing;
    }

    public void setNorthing(Integer northing) {
        this.northing = northing;
    }

    public HashMap<String, ArrayList<Measurement>> getMeasurementMap() {
        return measurementMap;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
