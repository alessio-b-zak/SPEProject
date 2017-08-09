package com.epimorphics.android.myrivers.data

/**
 * Data class storing individual characteristic of a waterbody found in Catchment Data Explorer.
 *
 * @property unit a measurement unit for a value
 * @property label a name of the characteristic
 * @property value a value of the characteristic
 *
 * @see MyArea
 */
data class Characteristic(val unit: String, val label: String, val value: Number)