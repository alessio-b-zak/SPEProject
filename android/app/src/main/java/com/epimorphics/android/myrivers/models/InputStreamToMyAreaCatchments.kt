package com.epimorphics.android.myrivers.models

import android.util.JsonReader
import com.epimorphics.android.myrivers.data.MyArea
import com.epimorphics.android.myrivers.helpers.InputStreamHelper
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by mihajlo on 04/07/17.
 */
class InputStreamToMyAreaCatchments : InputStreamHelper() {

    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream, myArea: MyArea) {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        reader.use {
            readMessagesArray(reader, myArea)
        }
    }

    @Throws(IOException::class)
    fun readMessagesArray(reader: JsonReader, myArea: MyArea) {
        reader.beginObject()
        while (reader.hasNext()) {
            var name = reader.nextName()
            if (name == "result") {
                reader.beginObject()
                while (reader.hasNext()) {
                    name = reader.nextName()
                    if (name == "primaryTopic") {
                        readMessage(reader, myArea)
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
    }

    @Throws(IOException::class)
    fun readMessage(reader: JsonReader, myArea: MyArea) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "inManagementCatchment" -> myArea.managementCatchment = readItemToString(reader)
                "inRiverBasinDistrict" -> myArea.riverBasinDistrict = readItemToString(reader)
                "label" -> myArea.operationalCatchment = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
    }
}