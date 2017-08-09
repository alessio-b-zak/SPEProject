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
 * A renderer class for a DischargePermitPoint marker
 *
 * @param context Application context
 * @param mMap GoogleMap
 * @param clusterManager ClusterManager
 * @see DischargePermitPoint
 */
class DischargePermitPointRenderer(context: Context, val mMap: GoogleMap, clusterManager: ClusterManager<DischargePermitPoint>) :
        DefaultClusterRenderer<DischargePermitPoint>(context, mMap, clusterManager) {

    /**
     * Sets marker icon from resource drawable
     *
     * @param permitPoint a DischargePermitPoint for which a marker icon is to be set
     * @param markerOptions a MarkerOptions of a given permitPoint
     */
    override fun onBeforeClusterItemRendered(permitPoint: DischargePermitPoint, markerOptions: MarkerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_permit_marker))
    }
}