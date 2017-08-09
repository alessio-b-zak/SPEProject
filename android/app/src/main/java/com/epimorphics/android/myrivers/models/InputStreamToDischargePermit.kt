package com.epimorphics.android.myrivers.models

import android.util.JsonReader
import com.epimorphics.android.myrivers.data.DischargePermitPoint
import com.epimorphics.android.myrivers.helpers.InputStreamHelper
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Consumes an InputStream and converts it to a List of DischargePermitPoints
 *
 * @see DischargePermitPoint
 */
class InputStreamToDischargePermit : InputStreamHelper() {

    /**
     * Converts InputStream to JsonReader and consumes it.
     *
     * @param inputStream InputStream to be consumed
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream): List<DischargePermitPoint> {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.use {
            return readMessagesArray(reader)
        }
    }

    /**
     * Focuses on the array of objects that are to be converted to DischargePermitPoints and parses
     * them one by one.
     *
     * @param reader JsonReader to be consumed
     * @return List<DischargePermitPoint> result
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessagesArray(reader: JsonReader): List<DischargePermitPoint> {
        val messages = ArrayList<DischargePermitPoint>()

        reader.beginArray()
        while (reader.hasNext()) {
            readMessage(reader, messages)
        }
        reader.endArray()

        return messages
    }

    /**
     * Converts single JsonObject to DischargePermitPoint and adds it to the given list of points.
     *
     * @param reader JsonReader to be consumed
     * @param messages an ArrayList of DischargePermitPoints to which parsed point is to be added
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessage(reader: JsonReader, messages: ArrayList<DischargePermitPoint>) {
        var id = ""
        var holder = ""
        var siteType = ""
        var effluentType = ""
        var effectiveDate: String = ""
        var revocationDate: String = ""
        var latitude: Double = 0.0
        var longitude: Double = 0.0

        reader.beginObject()

        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "id" -> id = reader.nextString()
                "holder" -> holder = reader.nextString()
                "siteType" -> siteType = reader.nextString()
                "effluentType" -> effluentType = reader.nextString()
                "effectiveDate" -> effectiveDate = reader.nextString()
                "revocationDate" -> revocationDate = reader.nextString()
                "latitude" -> latitude = reader.nextDouble()
                "longitude" -> longitude = reader.nextDouble()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        if (revocationDate == "") {
            val newPoint = DischargePermitPoint(id, holder, effluentType, siteType, effectiveDate,
                    latitude, longitude)
            messages.add(newPoint)
        }

    }

    /**
     * Returns a single DischargePermitPoint(used by MyAreaFragment) converted from JsonObject.
     *
     * @param reader JsonReader to be consumed
     * @return DischargePermitPoint containing a distance to the user's current location
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessageWithDistance(reader: JsonReader): DischargePermitPoint {
        var id = ""
        var holder = ""
        var siteType = ""
        var effluentType = ""
        var effectiveDate: String = ""
        var latitude: Double = 0.0
        var longitude: Double = 0.0
        var distance: Double = 0.0

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "id" -> id = reader.nextString()
                "holder" -> holder = reader.nextString()
                "siteType" -> siteType = reader.nextString()
                "effluentType" -> effluentType = reader.nextString()
                "effectiveDate" -> effectiveDate = reader.nextString()
                "latitude" -> latitude = reader.nextDouble()
                "longitude" -> longitude = reader.nextDouble()
                "distance" -> distance = reader.nextDouble()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return DischargePermitPoint(id, holder, effluentType, siteType, effectiveDate,
                latitude, longitude, distance)

    }

}