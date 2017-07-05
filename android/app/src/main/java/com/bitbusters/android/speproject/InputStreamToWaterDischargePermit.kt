package com.bitbusters.android.speproject

import android.util.JsonReader
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList

/**
 * Created by mihajlo on 04/07/17.
 */
class InputStreamToWaterDischargePermit {

    private val converterCoordinateSystem: CoordinateSystemConverter = CoordinateSystemConverter()

    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream): List<DischargePermitPoint> {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        try {
            return readMessagesArray(reader)
        } finally {
            reader.close()
        }
    }

    @Throws(IOException::class)
    fun readMessagesArray(reader: JsonReader): List<DischargePermitPoint> {
        val messages = ArrayList<DischargePermitPoint>()
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "items") {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        val permit = readMessage(reader)
                        if (permit.holderName != "") {
                            messages.add(permit)
                        }
                    }
                    reader.endArray()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return messages
    }

    @Throws(IOException::class)
    fun readMessage(reader: JsonReader): DischargePermitPoint {
        var holderName: String = ""
        var siteType: String = ""
        var effectiveDate: String = ""
        var revocationDate: String? = null
        var easting: Double = 0.0
        var northing: Double = 0.0
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                var name = reader.nextName()
                if (name == "holder") {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        name = reader.nextName()
                        if (name == "name") {
                            holderName = reader.nextString()
                        } else {
                            reader.skipValue()
                        }
                    }
                    reader.endObject()
                } else if (name == "site") {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        name = reader.nextName()
                        if (name == "location") {
                            reader.beginObject()
                            while (reader.hasNext()) {
                                name = reader.nextName()
                                if (name == "easting") {
                                    easting = reader.nextDouble()
                                } else if (name == "northing") {
                                    northing = reader.nextDouble()
                                } else {
                                    reader.skipValue()
                                }
                            }
                            reader.endObject()
                        } else if (name == "siteType") {
                            reader.beginObject()
                            while (reader.hasNext()) {
                                name = reader.nextName()
                                if (name == "comment") {
                                    siteType = reader.nextString()
                                } else {
                                    reader.skipValue()
                                }
                            }
                            reader.endObject()
                        } else {
                            reader.skipValue()
                        }
                    }
                    reader.endObject()
                } else if (name == "revocationDate") {
                    revocationDate = reader.nextString()
                } else if (name == "effectiveDate") {
                    effectiveDate = reader.nextString()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        Log.i("FIND ME", "$holderName $siteType $effectiveDate");

        if (revocationDate == null) {
            val location : LatLng = converterCoordinateSystem.convertEastNorthToLatLng(easting,northing)
            return DischargePermitPoint(holderName, siteType, effectiveDate,location.latitude, location.longitude)
        } else {
            return DischargePermitPoint("", "", "", 0.0, 0.0)
        }
    }
}