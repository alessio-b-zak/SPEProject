package com.bitbusters.android.speproject

import kotlin.properties.Delegates

/**
 * Created by mihajlo on 04/07/17.
 */
data class DischargePermitPoint(val id: String,
                                val holder: String,
                                val siteType: String,
                                val effluentType: String,
                                val effectiveDate: String,
                                val revocationDate: String,
                                val latitude: Double,
                                val longitude: Double) : Point(latitude, longitude, "Waste_Point", "") {
}