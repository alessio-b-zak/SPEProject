package com.bitbusters.android.speproject.renderers

import android.content.Context
import com.bitbusters.android.speproject.R
import com.bitbusters.android.speproject.data.WIMSPoint
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * Created by mihajlo on 04/07/17.
 */
class WIMSPointRenderer(context: Context, val mMap: GoogleMap, clusterManager: ClusterManager<WIMSPoint>) :
        DefaultClusterRenderer<WIMSPoint>(context, mMap, clusterManager) {

    override fun onBeforeClusterItemRendered(sp: WIMSPoint, markerOptions: MarkerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_wims_marker))
    }
}