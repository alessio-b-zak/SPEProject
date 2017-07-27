package com.bitbusters.android.speproject;

import android.util.Log;

import com.google.android.gms.common.api.BooleanResult;
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
    private WIMSPoint wimsPoint;
    private DischargePermitPoint permitPoint;

    private List<Boolean> isDataLoaded;

    private OnPopulated listener;

    MyArea() {
        this.waterbody = "";
        this.characteristicList = null;
        this.operationalCatchment = "";
        this.managementCatchment = "";
        this.riverBasinDistrict = "";
        this.wimsPoint = null;
        this.permitPoint = null;
        this.isDataLoaded = new ArrayList<Boolean>();
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

    public WIMSPoint getWIMSPoint() {
        return wimsPoint;
    }

    public DischargePermitPoint getPermitPoint() {
        return permitPoint;
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
        isDataLoaded.add(true);
        if(isDataLoaded.size() == 3 && listener != null) {
            listener.onPopulated();
        }
    }

    public void setCharacteristicList(ArrayList<Characteristic> characteristicList) {
        this.characteristicList = characteristicList;
    }

    public void setWimsPoint(WIMSPoint wimsPoint) {
        this.wimsPoint = wimsPoint;
        isDataLoaded.add(true);
        if(isDataLoaded.size() == 3 && listener != null) {
            listener.onPopulated();
        }
    }

    public void setPermitPoint(DischargePermitPoint permitPoint) {
        this.permitPoint = permitPoint;
        isDataLoaded.add(true);
        if(isDataLoaded.size() == 3 && listener != null) {
            listener.onPopulated();
        }
    }

    public OnPopulated getOnPopulatedListener() {
        return listener;
    }

    public void setOnPopulatedListener(OnPopulated listener) {
        this.listener = listener;
    }

}
