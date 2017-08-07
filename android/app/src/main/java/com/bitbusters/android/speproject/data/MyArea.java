package com.bitbusters.android.speproject.data;

import com.bitbusters.android.speproject.interfaces.OnPopulated;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihajlo on 25/07/2017.
 */

public class MyArea {
    private String waterbody;
    private String operationalCatchment;
    private String managementCatchment;
    private String riverBasinDistrict;
    private ArrayList<Characteristic> characteristicList;
    private Boolean hasWaterbody;
    private WIMSPoint wimsPoint;
    private DischargePermitPoint permitPoint;

    private List<Boolean> isDataLoaded;

    private OnPopulated listener;

    public MyArea() {
        this.waterbody = null;
        this.characteristicList = null;
        this.operationalCatchment = null;
        this.managementCatchment = null;
        this.riverBasinDistrict = null;
        this.wimsPoint = null;
        this.permitPoint = null;
        this.hasWaterbody = false;
        this.isDataLoaded = new ArrayList<Boolean>();
    }

    public String getWaterbody() {
        return waterbody;
    }

    public void setWaterbody(String waterbody) {
        this.waterbody = waterbody;
    }

    public String getOperationalCatchment() {
        return operationalCatchment;
    }

    public void setOperationalCatchment(String operationalCatchment) {
        this.operationalCatchment = operationalCatchment;
    }

    public String getManagementCatchment() {
        return managementCatchment;
    }

    public void setManagementCatchment(String managementCatchment) {
        this.managementCatchment = managementCatchment;
    }

    public String getRiverBasinDistrict() {
        return riverBasinDistrict;
    }

    public Boolean getHasWaterbody() {
        return hasWaterbody;
    }

    public WIMSPoint getWIMSPoint() {
        return wimsPoint;
    }

    public DischargePermitPoint getPermitPoint() {
        return permitPoint;
    }

    public void setRiverBasinDistrict(String riverBasinDistrict) {
        this.riverBasinDistrict = riverBasinDistrict;
        isDataLoaded.add(true);
        if (isDataLoaded.size() == 3 && listener != null) {
            listener.onPopulated();
        }
    }

    public void setHasWaterbody(boolean hasWaterbody) {
        this.hasWaterbody = hasWaterbody;
        if(!hasWaterbody) {
            isDataLoaded.add(true);
        }
    }

    public void setPermitPoint(DischargePermitPoint permitPoint) {
        this.permitPoint = permitPoint;
        isDataLoaded.add(true);
        if (isDataLoaded.size() == 3 && listener != null) {
            listener.onPopulated();
        }
    }

    public ArrayList<Characteristic> getCharacteristicList() {
        return characteristicList;
    }

    public void setCharacteristicList(ArrayList<Characteristic> characteristicList) {
        this.characteristicList = characteristicList;
    }

    public void setWimsPoint(WIMSPoint wimsPoint) {
        this.wimsPoint = wimsPoint;
        isDataLoaded.add(true);
        if (isDataLoaded.size() == 3 && listener != null) {
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
