package com.bitbusters.android.speproject

import android.util.JsonReader
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.collections.ArrayList

/**
 * Created by mihajlo on 04/07/17.
 */
class InputStreamToDischargePermit {
    private val TAG = "DISCHARGE_PERMIT_READER"

    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream): List<DischargePermitPoint> {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
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
            reader.beginArray()
            while (reader.hasNext()) {
                readMessage(reader, messages)
            }
            reader.endArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return messages
    }

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
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "id") {
                    id = reader.nextString()
                } else if (name == "holder") {
                    holder = reader.nextString()
                } else if (name == "siteType") {
                    siteType = reader.nextString()
                } else if (name == "effluentType") {
                    effluentType = reader.nextString()
                } else if (name == "effectiveDate") {
                    effectiveDate = reader.nextString()
                } else if (name == "revocationDate") {
                    revocationDate = reader.nextString()
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

        if(revocationDate != "") {
            val newPoint = DischargePermitPoint(id, holder, effluentType, siteType, effectiveDate,
                                                latitude, longitude)
            messages.add(newPoint)
        }

    }

}