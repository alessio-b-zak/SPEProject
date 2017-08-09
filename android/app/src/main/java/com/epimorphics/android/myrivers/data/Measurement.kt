package com.epimorphics.android.myrivers.data

/**
 * Data class storing individual measurement of a sampling point found in Water Quality Data Archive.
 *
 * @property unit a unit of the measurement
 * @property result a result of the measurement
 * @property descriptor measurement descriptor
 * @property date date of the measurement
 *
 * @see WIMSPoint
 */
data class Measurement(val unit: String, val result: Double, val descriptor: String, val date: String)