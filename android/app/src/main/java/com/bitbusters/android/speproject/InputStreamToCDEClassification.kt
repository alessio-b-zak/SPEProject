package com.bitbusters.android.speproject

import android.icu.util.Measure
import android.util.JsonReader
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by mihajlo on 04/07/17.
 */
class InputStreamToCDEClassification: InputStreamHelper() {

    @Throws(IOException::class)
    fun readJsonStream(cdePoint: CDEPoint, inputStream: InputStream, group: String) {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        reader.use {
            readMessagesArray(cdePoint, reader, group)
        }
    }

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

    @Throws(IOException::class)
    fun readMessage(cdePoint: CDEPoint, reader: JsonReader, group: String) {
        var item = ""
        var certainty = ""
        var value = ""
        var year = ""

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when(name){
                "classificationCertainty" -> certainty = readItemToString(reader)
                "classificationItem"      -> item = readItemToString(reader)
                "classificationValue"     -> value = readItemToString(reader)
                "classificationYear"      -> year = reader.nextString()
                else                      -> reader.skipValue()
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