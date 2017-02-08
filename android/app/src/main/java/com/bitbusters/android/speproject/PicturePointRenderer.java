package com.bitbusters.android.speproject;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

/**
 * Created by Stefan on 08/02/2017.
 */

public class PicturePointRenderer extends DefaultClusterRenderer<PicturePoint> {



    public PicturePointRenderer(Context context, GoogleMap map, ClusterManager<PicturePoint> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(PicturePoint pp, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<PicturePoint> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
        //THIS IS WHERE TO CUSTOMISE THE PICTURE BLUSTER

    }



}
