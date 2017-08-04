package com.bitbusters.android.speproject.models

import android.util.JsonReader
import com.bitbusters.android.speproject.data.WIMSPoint
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * Created by mihajlo on 04/07/17.
 */
class InputStreamToWIMSPoint {
    private val TAG = "WIMS_POINTS_READER"

    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream): List<WIMSPoint> {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.use {
            return readMessagesArray(reader)
        }
    }

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