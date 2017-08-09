package com.epimorphics.android.myrivers.data

/**
 * Data class storing individual Reason for Not Achieving Good of a waterbody found in Catchment Data Explorer.
 *
 * @property element element name
 * @property rating rating
 * @property activity corresponding activity causing Not Achieving Good
 * @property category category
 * @property year year
 *
 * @see CDEPoint
 */
data class RNAG(val element: String, val rating: String, val activity: String, val category: String, val year: Int)