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
class InputStreamToDischargePermit: InputStreamHelper() {
    private val TAG = "DISCHARGE_PERMIT_READER"

    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream): List<DischargePermitPoint> {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.use {
            return readMessagesArray(reader)
        }
    }

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
            when(name){
                "id"              -> id = reader.nextString()
                "holder"          -> holder = reader.nextString()
                "siteType"        -> siteType = reader.nextString()
                "effluentType"    -> effluentType = reader.nextString()
                "effectiveDate"   -> effectiveDate = reader.nextString()
                "revocationDate"  -> revocationDate = reader.nextString()
                "latitude"        -> latitude = reader.nextDouble()
                "longitude"       -> longitude = reader.nextDouble()
                else              -> reader.skipValue()
            }
        }
        reader.endObject()

        if(revocationDate != "") {
            val newPoint = DischargePermitPoint(id, holder, effluentType, siteType, effectiveDate,
                                                latitude, longitude)
            messages.add(newPoint)
        }

    }

}