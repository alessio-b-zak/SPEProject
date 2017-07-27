package com.bitbusters.android.speproject;

import java.util.List;
/**
 * Created by cp153 on 15/01/2017.
 */

public interface OnTaskCompleted {
    void onTaskCompletedWIMSPoint(List<WIMSPoint> result);
    void onTaskCompletedCDEPoint(List<CDEPoint> result);
    void onTaskCompletedDischargePermitPoint(List<DischargePermitPoint> result);
    void onTaskCompletedCDERiverLine(CDEPoint result);
    void onTaskCompletedMyAreaWIMS(WIMSPoint wimsPoint);
    void onTaskCompletedMyAreaPermit(DischargePermitPoint permitPoint);
    void onTaskCompletedMyAreaCDE();
}
