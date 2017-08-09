package com.epimorphics.android.myrivers.helpers;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class allowing Markers of different type to be shown on the GoogleMap at the same time
 *
 * @see com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
 * @see com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
 *
 */
public class MultiListener implements GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {

    private List<GoogleMap.OnMarkerClickListener> onClickListeners = new ArrayList<GoogleMap.OnMarkerClickListener>();
    private List<GoogleMap.OnCameraIdleListener> cameraIdleListeners = new ArrayList<>();

    /**
     * Adds OnMarkerClickListener to the onClickListeners
     *
     * @param listener OnMarkerClickListener to be added to the onClickListeners
     */
    public void addOM(GoogleMap.OnMarkerClickListener listener) {
        onClickListeners.add(listener);
    }

    /**
     * Adds OnCameraIdleListener to the cameraIdleListeners
     *
     * @param listener OnCameraIdleListener to be added to the cameraIdleListeners
     */
    public void addOC(GoogleMap.OnCameraIdleListener listener) {
        cameraIdleListeners.add(listener);
    }

    /**
     * Calls onMarkerClick listener and hides info window
     *
     * @param marker clicked marker
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        for (GoogleMap.OnMarkerClickListener m : onClickListeners) {
            m.onMarkerClick(marker);
            marker.hideInfoWindow();
        }
        return true;
    }

    /**
     * Calls onCameraIdle listener on all currently held cameraIdleListeners
     */
    @Override
    public void onCameraIdle() {
        for (GoogleMap.OnCameraIdleListener m : cameraIdleListeners) {
            m.onCameraIdle();
        }
    }
}
