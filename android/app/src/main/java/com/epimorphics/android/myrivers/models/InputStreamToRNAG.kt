package com.epimorphics.android.myrivers.models

import android.util.JsonReader
import com.epimorphics.android.myrivers.data.CDEPoint
import com.epimorphics.android.myrivers.data.RNAG
import com.epimorphics.android.myrivers.helpers.InputStreamHelper
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Consumes an InputStream and converts it to a list of RNAGs
 *
 * @see CDEPoint
 * @see RNAG
 */
class InputStreamToRNAG : InputStreamHelper() {

    /**
     * Converts InputStream to JsonReader and consumes it.
     *
     * @param cdePoint CDEPoint to which parsed RNAGs belong
     * @param inputStream InputStream to be consumed
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readJsonStream(cdePoint: CDEPoint, inputStream: InputStream) {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        reader.use {
            readMessagesArray(cdePoint, reader)
        }
    }

    /**
     * Focuses on the array of objects that are to be converted to RNAGs and parses
     * them one by one.
     *
     * @param cdePoint CDEPoint to which parsed RNAGs belong
     * @param reader JsonReader to be consumed
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessagesArray(cdePoint: CDEPoint, reader: JsonReader) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "items") {
                reader.beginArray()
                while (reader.hasNext()) {
                    readMessage(cdePoint, reader)
                }
                reader.endArray()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
    }

    /**
     * Converts single JsonObject to RNAG and adds it to the given CDEPoint's rnagList.
     *
     * @param cdePoint CDEPoint to which parsed RNAG belongs
     * @param reader JsonReader to be consumed
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessage(cdePoint: CDEPoint, reader: JsonReader) {
        var element = ""
        var rating = ""
        var activity = ""
        var category = ""
        var year = ""

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "activity" -> activity = readItemToString(reader)
                "classificationItem" -> element = readItemToString(reader)
                "classification" -> rating = readClassificationToString(reader)
                "classificationYear" -> year = reader.nextString()
                "category" -> category = readItemToString(reader)
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        cdePoint.addRNAG(RNAG(element, rating, activity, category, year.toInt()))
    }

    /**
     * Reads group value from the reader and returns it as a string
     *
     * @param reader JsonReader to be consumed
     * @return String group value
     *
     * @throws IOException
     */
    fun readClassificationToString(reader: JsonReader): String {
        var item = ""

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "classificationValue") {
                item = readItemToString(reader)
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()

        return item
    }
}