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
class InputStreamToWIMSPoint {
    private val TAG = "WIMS_POINTS_READER"

    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream): List<WIMSPoint> {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        try {
            return readMessagesArray(reader)
        } finally {
            reader.close()
        }
    }

    @Throws(IOException::class)
    fun readMessagesArray(reader: JsonReader): List<WIMSPoint> {
        val messages = ArrayList<WIMSPoint>()
        try {
            reader.beginArray()
            while (reader.hasNext()) {
                messages.add(readMessage(reader))
            }
            reader.endArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return messages
    }

    @Throws(IOException::class)
    fun readMessage(reader: JsonReader): WIMSPoint {
        var id: String = ""
        var latitude = 0.0
        var longitude = 0.0
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "id") {
                    id = reader.nextString()
                } else if (name == "latitude") {
                    latitude = reader.nextDouble()
                } else if (name == "longitude") {
                    longitude = reader.nextDouble()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return WIMSPoint(id, latitude, longitude)
    }

}