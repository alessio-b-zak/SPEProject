package com.bitbusters.android.speproject;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cp153 on 06/12/2016.
 */

public class WIMSPoint extends Point {
    private String id;
    private String type;
    private String label;
    private Integer easting;
    private Integer northing;
    private Double distance;
    private HashMap<String, ArrayList<Measurement>> measurementMap;

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



    WIMSPoint(String id, double latitude, double longitude, String type,
              String label, Integer easting, Integer northing) {
        super(latitude,longitude,"WIMS_Point", "");
        this.id = id;
        this.type = type;
        this.label = label;
        this.easting = easting;
        this.northing = northing;
        this.distance = 0.0;
        this.measurementMap = new HashMap<>();
    }

    WIMSPoint(String id, double latitude, double longitude) {
        super(latitude,longitude,"WIMS_Point", "");
        this.id = id;
        this.type = null;
        this.label = null;
        this.easting = null;
        this.northing = null;
        this.distance = 0.0;
        this.measurementMap = new HashMap<String, ArrayList<Measurement>>();
    }

    WIMSPoint(String id, double latitude, double longitude, double distance) {
        super(latitude,longitude,"WIMS_Point", "");
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

    public double getLatitude() {
        return this.getPosition().latitude;
    }

    public double getLongitude() {
        return this.getPosition().longitude;
    }

    public double getDistance() {
        return distance;
    }

    public String getType() {
        return type;
    }

    public Integer getEasting() { return easting; }

    public Integer getNorthing() { return northing; }

    public HashMap<String, ArrayList<Measurement>> getMeasurementMap() { return measurementMap; }

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
}
