package com.bitbusters.android.speproject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.geojson.GeoJsonFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

/**
 * Created by mihajlo on 25/07/2017.
 */

public class MyArea {
    private String waterbody;
    private String operationalCatchment;
    private String managementCatchment;
    private String riverBasinDistrict;
    private ArrayList<Characteristic> characteristicList;

    MyArea(String waterbody, String operationalCatchment,
           ArrayList<Characteristic> characteristicList) {
        this.waterbody = waterbody;
        this.characteristicList = characteristicList;
        this.operationalCatchment = operationalCatchment;
        this.managementCatchment = "";
        this.riverBasinDistrict = "";
    }

    public String getWaterbody() {
        return waterbody;
    }

    public String getOperationalCatchment() {
        return operationalCatchment;
    }

    public String getManagementCatchment() {
        return managementCatchment;
    }

    public String getRiverBasinDistrict() {
        return riverBasinDistrict;
    }

    public ArrayList<Characteristic> getCharacteristicList() {
        return characteristicList;
    }

    public void setWaterbody(String waterbody) {
        this.waterbody = waterbody;
    }

    public void setOperationalCatchment(String operationalCatchment) {
        this.operationalCatchment = operationalCatchment;
    }

    public void setManagementCatchment(String managementCatchment) {
        this.managementCatchment = managementCatchment;
    }

    public void setRiverBasinDistrict(String riverBasinDistrict) {
        this.riverBasinDistrict = riverBasinDistrict;
    }

}
