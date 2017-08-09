package com.epimorphics.android.myrivers.renderers

import android.content.Context
import com.epimorphics.android.myrivers.R
import com.epimorphics.android.myrivers.data.WIMSPoint
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * A renderer class for a WIMSPoint marker
 *
 * @param context Application context
 * @param mMap GoogleMap
 * @param clusterManager ClusterManager
 *
 * @see WIMSPoint
 */
class WIMSPointRenderer(context: Context, val mMap: GoogleMap, clusterManager: ClusterManager<WIMSPoint>) :
        DefaultClusterRenderer<WIMSPoint>(context, mMap, clusterManager) {
    /**
     * Sets marker icon from resource drawable
     *
     * @param wimsPoint a DischargePermitPoint for which a marker icon is to be set
     * @param markerOptions a MarkerOptions of a given wimsPoint
     */
    override fun onBeforeClusterItemRendered(wimsPoint: WIMSPoint, markerOptions: MarkerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_wims_marker))
    }
}