package com.bitbusters.android.speproject;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Stefan on 08/02/2017.
 */

public class WIMSPointRenderer extends DefaultClusterRenderer<WIMSPoint>{

    private GoogleMap mMap;

    public WIMSPointRenderer(Context context, GoogleMap map, ClusterManager<WIMSPoint> clusterManager) {
        super(context, map, clusterManager);
        this.mMap = map;
    }

    @Override
    protected void onBeforeClusterItemRendered(WIMSPoint sp, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
    }





}
