package com.epimorphics.android.myrivers.data

/**
 * Data class storing individual group of a waterbody found in Catchment Data Explorer.
 *
 * @property value a value of the group
 * @property certainty a descriptive certainty of the group
 * @property year a year of the group
 *
 * @see CDEPoint
 */
data class Classification(val value: String, val certainty: String, val year: String)