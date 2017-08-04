package com.bitbusters.android.speproject.data

/**
 * Created by mihajlo on 04/07/17.
 */
data class DischargePermitPoint(val id: String,
                                val holder: String,
                                val effluentType: String,
                                val siteType: String,
                                val effectiveDate: String,
                                val latitude: Double,
                                val longitude: Double,
                                val distance: Double = 0.0) : Point(latitude, longitude, "Waste_Point", "") {
}