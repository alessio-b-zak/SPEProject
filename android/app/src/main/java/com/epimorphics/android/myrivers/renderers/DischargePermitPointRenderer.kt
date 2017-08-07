package com.epimorphics.android.myrivers.renderers

import android.content.Context
import com.epimorphics.android.myrivers.R
import com.epimorphics.android.myrivers.data.DischargePermitPoint
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
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_permit_marker))
    }
}