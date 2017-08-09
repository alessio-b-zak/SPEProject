package com.epimorphics.android.myrivers.interfaces;

import com.epimorphics.android.myrivers.data.CDEPoint;
import com.epimorphics.android.myrivers.data.DischargePermitPoint;
import com.epimorphics.android.myrivers.data.WIMSPoint;
import com.epimorphics.android.myrivers.apis.*;

import java.util.List;

/**
 * The OnTaskCompleted interface contains methods called after consuming input stream of an API call.
 */
public interface OnTaskCompleted {
    /**
     * Called after a successful API call is made in WimsPointAPI
     *
     * @param result List<WIMSPoint> created after an API call in WimsPointAPI
     * @see WIMSPointAPI
     */
    void onTaskCompletedWIMSPoint(List<WIMSPoint> result);

    /**
     * Called after a successful API call is made in WimsPointAPI
     *
     * @param result List<CDEPoint> created after an API call in WimsPointAPI
     * @see CDEPointAPI
     */
    void onTaskCompletedCDEPoint(List<CDEPoint> result);

    /**
     * Called after a successful API call is made in DischargePermitPointAPI
     *
     * @param result List<DischargePermitPoint> created after an API call in DischargePermitPointAPI
     * @see DischargePermitPointAPI
     */
    void onTaskCompletedDischargePermitPoint(List<DischargePermitPoint> result);

    /**
     * Called after a successful API call is made in CDERiverLineAPI
     *
     * @param result CDEPoint containing a river line obtained from an API call in CDERiverLineAPI
     * @see CDERiverLineAPI
     */
    void onTaskCompletedCDERiverLine(CDEPoint result);

    /**
     * Called after a successful API call is made in MyAreaNearestWIMSAPI
     *
     * @param result WIMSPoint created after an API call in MyAreaNearestWIMSAPI
     * @see MyAreaNearestWIMSAPI
     */
    void onTaskCompletedMyAreaWIMS(WIMSPoint result);

    /**
     * Called after a successful API call is made in MyAreaNearestPermitAPI
     *
     * @param result DischargePermitPoint created after an API call in MyAreaNearestPermitAPI
     * @see MyAreaNearestPermitAPI
     */
    void onTaskCompletedMyAreaPermit(DischargePermitPoint result);

    /**
     * Called after a successful API call is made in MyAreaCDEAPI
     *
     * @see MyAreaCDEAPI
     */
    void onTaskCompletedMyAreaCDE();
}
