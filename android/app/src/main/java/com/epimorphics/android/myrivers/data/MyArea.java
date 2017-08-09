package com.epimorphics.android.myrivers.data;

import com.epimorphics.android.myrivers.interfaces.OnPopulated;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class storing details of the nearest waterbody(including characteristics), water quality
 * sampling point and pollution permit holder
 *
 * @see WIMSPoint
 * @see DischargePermitPoint
 * @see CDEPoint
 * @see Characteristic
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

    /**
     * Data constructor which initialises all properties to null
     */
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

    /**
     * @return String waterbody
     */
    public String getWaterbody() {
        return waterbody;
    }

    /**
     * @param waterbody waterbody name
     */
    public void setWaterbody(String waterbody) {
        this.waterbody = waterbody;
    }

    /**
     * @return String operationalCatchment
     */
    public String getOperationalCatchment() {
        return operationalCatchment;
    }

    /**
     * @param operationalCatchment waterbody operationalCatchment
     */
    public void setOperationalCatchment(String operationalCatchment) {
        this.operationalCatchment = operationalCatchment;
    }

    /**
     * @return String managementCatchment
     */
    public String getManagementCatchment() {
        return managementCatchment;
    }

    /**
     * @param managementCatchment waterbody managementCatchment
     */
    public void setManagementCatchment(String managementCatchment) {
        this.managementCatchment = managementCatchment;
    }

    /**
     * @return String riverBasinDistrict
     */
    public String getRiverBasinDistrict() {
        return riverBasinDistrict;
    }

    /**
     * @return Boolean hasWaterbody
     */
    public Boolean getHasWaterbody() {
        return hasWaterbody;
    }

    /**
     * @return WIMSPoint wimsPoint
     * @see WIMSPoint
     */
    public WIMSPoint getWIMSPoint() {
        return wimsPoint;
    }

    /**
     * @return DischargePermitPoint permitPoint
     * @see DischargePermitPoint
     */
    public DischargePermitPoint getPermitPoint() {
        return permitPoint;
    }

    /**
     * If the nearest for all CDE, WIMS and EPR is loaded fire the listener.onMyAreaPopulated()
     *
     * @param riverBasinDistrict waterbody riverBasinDistrict(CDE)
     * @see OnPopulated
     */
    public void setRiverBasinDistrict(String riverBasinDistrict) {
        this.riverBasinDistrict = riverBasinDistrict;
        isDataLoaded.add(true);
        if (isDataLoaded.size() == 3 && listener != null) {
            listener.onMyAreaPopulated();
        }
    }

    /**
     * If no waterbody found add true to isDataLoaded to allow listener to fire when the rest of
     * the data is loaded
     *
     * @param hasWaterbody a boolean stating if waterbody is found
     */
    public void setHasWaterbody(boolean hasWaterbody) {
        this.hasWaterbody = hasWaterbody;
        if(!hasWaterbody) {
            isDataLoaded.add(true);
            if (isDataLoaded.size() == 3 && listener != null) {
                listener.onMyAreaPopulated();
            }
        }
    }

    /**
     * If the nearest for all CDE, WIMS and EPR is loaded fire the listener.onMyAreaPopulated()
     *
     * @param permitPoint nearest permitPoint(EPR)
     * @see OnPopulated
     */
    public void setPermitPoint(DischargePermitPoint permitPoint) {
        this.permitPoint = permitPoint;
        isDataLoaded.add(true);
        if (isDataLoaded.size() == 3 && listener != null) {
            listener.onMyAreaPopulated();
        }
    }

    /**
     * @return ArrayList<Characteristic> characteristicList
     */
    public ArrayList<Characteristic> getCharacteristicList() {
        return characteristicList;
    }

    /**
     * @param characteristicList a list of river catchment area characteristics
     */
    public void setCharacteristicList(ArrayList<Characteristic> characteristicList) {
        this.characteristicList = characteristicList;
    }

    /**
     * If the nearest for all CDE, WIMS and EPR is loaded fire the listener.onMyAreaPopulated()
     *
     * @param wimsPoint nearest wimsPoint(WIMS)
     * @see OnPopulated
     */
    public void setWimsPoint(WIMSPoint wimsPoint) {
        this.wimsPoint = wimsPoint;
        isDataLoaded.add(true);
        if (isDataLoaded.size() == 3 && listener != null) {
            listener.onMyAreaPopulated();
        }
    }

    /**
     * @param listener OnPopulated listener
     */
    public void setOnPopulatedListener(OnPopulated listener) {
        this.listener = listener;
    }

}
