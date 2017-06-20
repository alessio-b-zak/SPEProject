package com.bitbusters.android.speproject;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Stefan on 08/02/2017.
 */

public class SamplingPointRenderer extends DefaultClusterRenderer<SamplingPoint>{

    private GoogleMap mMap;

    public SamplingPointRenderer(Context context, GoogleMap map, ClusterManager<SamplingPoint> clusterManager) {
        super(context, map, clusterManager);
        this.mMap = map;
    }

    @Override
    protected void onBeforeClusterItemRendered(SamplingPoint sp, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
    }





}
