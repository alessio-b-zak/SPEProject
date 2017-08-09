package com.epimorphics.android.myrivers.models

import android.util.JsonReader
import com.epimorphics.android.myrivers.data.CDEPoint
import com.epimorphics.android.myrivers.data.Classification
import com.epimorphics.android.myrivers.helpers.InputStreamHelper
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Consumes an InputStream and converts it to Classification
 *
 * @see Classification
 */
class InputStreamToCDEClassification : InputStreamHelper() {

    /**
     * Converts InputStream to JsonReader and consumes it.
     *
     * @param cdePoint CDEPoint to which parsed Classification belongs
     * @param inputStream InputStream to be consumed
     * @param group a name of the group of the Classifications
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readJsonStream(cdePoint: CDEPoint, inputStream: InputStream, group: String) {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        reader.use {
            readMessagesArray(cdePoint, reader, group)
        }
    }

    /**
     * Focuses on the array of objects that are to be converted to Classifications and parses
     * them one by one.
     *
     * @param cdePoint CDEPoint to which parsed Classification belongs
     * @param reader JsonReader to be consumed
     * @param group a name of the group of the Classifications
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessagesArray(cdePoint: CDEPoint, reader: JsonReader, group: String) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "items") {
                reader.beginArray()
                while (reader.hasNext()) {
                    readMessage(cdePoint, reader, group)
                }
                reader.endArray()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
    }

    /**
     * Converts single JsonObject to Classification and adds it to the given CDEPoint's
     * classificationHashMap for a given group.
     *
     * @param cdePoint CDEPoint to which parsed Classification belongs
     * @param reader JsonReader to be consumed
     * @param group a name of the group of the Classifications
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessage(cdePoint: CDEPoint, reader: JsonReader, group: String) {
        var item = ""
        var certainty = ""
        var value = ""
        var year = ""

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "classificationCertainty" -> certainty = readItemToString(reader)
                "classificationItem" -> item = readItemToString(reader)
                "classificationValue" -> value = readItemToString(reader)
                "classificationYear" -> year = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        if (certainty == "No Information") {
            certainty = "No Info"
        }

        if (!cdePoint.getClassificationHashMap(group).containsKey(item)) {
            cdePoint.getClassificationHashMap(group).put(item, Classification(value, certainty, year))
        }

    }
}