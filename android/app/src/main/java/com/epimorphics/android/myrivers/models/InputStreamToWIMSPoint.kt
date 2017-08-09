package com.epimorphics.android.myrivers.models

import android.util.JsonReader
import com.epimorphics.android.myrivers.data.WIMSPoint
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * Consumes an InputStream and converts it to a List of WIMSPoints
 *
 * @see WIMSPoint
 */
class InputStreamToWIMSPoint {

    /**
     * Converts InputStream to JsonReader and consumes it.
     *
     * @param inputStream InputStream to be consumed
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream): List<WIMSPoint> {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.use {
            return readMessagesArray(reader)
        }
    }

    /**
     * Focuses on the array of objects that are to be converted to WIMSPoints and parses
     * them one by one.
     *
     * @param reader JsonReader to be consumed
     * @return List<WIMSPoint> result
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessagesArray(reader: JsonReader): List<WIMSPoint> {
        val messages = ArrayList<WIMSPoint>()

        reader.beginArray()
        while (reader.hasNext()) {
            messages.add(readMessage(reader))
        }
        reader.endArray()

        return messages
    }

    /**
     * Converts single JsonObject to WIMSPoint and returns it.
     *
     * @param reader JsonReader to be consumed
     * @param withDistance a Boolean flag set to true when a distance to the user's location is
     *                     needed(in MyArea) and false otherwise
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessage(reader: JsonReader, withDistance: Boolean = false): WIMSPoint {
        var id: String = ""
        var latitude = 0.0
        var longitude = 0.0
        var distance = 0.0

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "id" -> id = reader.nextString()
                "latitude" -> latitude = reader.nextDouble()
                "longitude" -> longitude = reader.nextDouble()
                "distance" -> distance = reader.nextDouble()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        if (withDistance) {
            return WIMSPoint(id, latitude, longitude, distance)
        } else {
            return WIMSPoint(id, latitude, longitude)
        }
    }

}