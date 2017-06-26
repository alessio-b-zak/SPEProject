package com.bitbusters.android.speproject;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by mihajlo on 21/06/2017.
 */

public class CDEPointRenderer extends DefaultClusterRenderer<CDEPoint>{

    private GoogleMap mMap;

    public CDEPointRenderer(Context context, GoogleMap map, ClusterManager<CDEPoint> clusterManager) {
        super(context, map, clusterManager);
        this.mMap = map;
    }

    @Override
    protected void onBeforeClusterItemRendered(CDEPoint cp, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cde_marker));
    }

}
