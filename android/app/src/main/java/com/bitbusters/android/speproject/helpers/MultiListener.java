package com.bitbusters.android.speproject.helpers;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 08/02/2017.
 */

public class MultiListener implements GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {

    List<GoogleMap.OnMarkerClickListener> onClickListeners = new ArrayList<GoogleMap.OnMarkerClickListener>();
    List<GoogleMap.OnCameraIdleListener> cameraIdleListeners = new ArrayList<>();

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (GoogleMap.OnMarkerClickListener m : onClickListeners) {
            m.onMarkerClick(marker);
            marker.hideInfoWindow();
        }
        return true;
    }

    public void addOM(GoogleMap.OnMarkerClickListener l) {
        onClickListeners.add(l);
    }

    public void addOC(GoogleMap.OnCameraIdleListener l) {
        cameraIdleListeners.add(l);
    }

    @Override
    public void onCameraIdle() {
        for (GoogleMap.OnCameraIdleListener m : cameraIdleListeners) {
            m.onCameraIdle();
        }
    }
}
