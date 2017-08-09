package com.epimorphics.android.myrivers.data

/**
 * Data class storing permit details found in Electronic Public Register(ePR).
 * Extends Point.
 *
 * @property id permit identifier
 * @property holder permit holder name
 * @property effluentType permit effluent type
 * @property siteType permit site type
 * @property effectiveDate permit effective date
 * @property latitude latitude
 * @property longitude longitude
 * @property distance distance to the user's location
 *
 * @see <a href="https://environment.data.gov.uk/public-register/view/index">Electronic Public Register</a>
 * @see Point
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