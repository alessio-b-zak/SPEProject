package com.bitbusters.android.speproject

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * Created by mihajlo on 04/07/17.
 */
class DischargePermitPointRenderer(context: Context, val mMap: GoogleMap, clusterManager: ClusterManager<DischargePermitPoint>) :
        DefaultClusterRenderer<DischargePermitPoint>(context, mMap, clusterManager) {

    override fun onBeforeClusterItemRendered(permitPoint: DischargePermitPoint, markerOptions: MarkerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
    }
}