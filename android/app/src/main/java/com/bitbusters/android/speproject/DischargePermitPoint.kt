package com.bitbusters.android.speproject

import kotlin.properties.Delegates

/**
 * Created by mihajlo on 04/07/17.
 */
data class DischargePermitPoint(val holderName: String, val siteType: String,
                                val effectiveDate: String, val latitude: Double,
                                val longitude: Double) : Point(latitude,longitude,"Waste_Point","") {
}