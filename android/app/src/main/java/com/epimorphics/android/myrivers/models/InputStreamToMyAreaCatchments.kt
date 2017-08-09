package com.epimorphics.android.myrivers.models

import android.util.JsonReader
import com.epimorphics.android.myrivers.data.MyArea
import com.epimorphics.android.myrivers.helpers.InputStreamHelper
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Consumes an InputStream and populates relevant fields of MyArea relating to the nearest river
 * catchment
 *
 * @see MyArea
 */
class InputStreamToMyAreaCatchments : InputStreamHelper() {

    /**
     * Converts InputStream to JsonReader and consumes it.
     *
     * @param inputStream InputStream to be consumed
     * @param myArea an object which is to be populated with catchment data
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream, myArea: MyArea) {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        reader.use {
            readMessagesArray(reader, myArea)
        }
    }

    /**
     * Focuses on the object that contains relevant data to be converted to MyArea fields.
     *
     * @param reader JsonReader to be consumed
     * @param myArea an object which is to be populated with catchment data
     *
     * @throws IOException
     */
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

    /**
     * Converts single JsonObject to relevant MyArea catchment records..
     *
     * @param reader JsonReader to be consumed
     * @param myArea an object which is to be populated with catchment data
     *
     * @throws IOException
     */
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