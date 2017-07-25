package com.bitbusters.android.speproject

import android.util.JsonReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by mihajlo on 12/07/17.
 */
class InputStreamToRNAG: InputStreamHelper() {

    @Throws(IOException::class)
    fun readJsonStream(cdePoint: CDEPoint, inputStream: InputStream) {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        reader.use {
            readMessagesArray(cdePoint, reader)
        }
    }

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
            when(name){
                "activity"           -> activity = readItemToString(reader)
                "classificationItem" -> element = readItemToString(reader)
                "classification"     -> rating = readClassificationToString(reader)
                "classificationYear" -> year = reader.nextString()
                "category"           -> category = readItemToString(reader)
                else                 -> reader.skipValue()
            }
        }
        reader.endObject()

        cdePoint.addRNAG(RNAG(element, rating, activity, category, year.toInt()))
    }

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