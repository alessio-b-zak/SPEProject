package com.bitbusters.android.speproject

import android.util.JsonReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by mihajlo on 12/07/17.
 */
class InputStreamToRNAG {

    @Throws(IOException::class)
    fun readJsonStream(cdePoint: CDEPoint, inputStream: InputStream) {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        try {
            readMessagesArray(cdePoint, reader)
        } finally {
            reader.close()
        }
    }

    @Throws(IOException::class)
    fun readMessagesArray(cdePoint: CDEPoint, reader: JsonReader) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    fun readMessage(cdePoint: CDEPoint, reader: JsonReader) {
        var element = ""
        var rating = ""
        var activity = ""
        var category = ""
        var year = ""
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "activity") {
                    activity = readItemToString(reader)
                } else if (name == "classificationItem") {
                    element = readItemToString(reader)
                } else if (name == "classification") {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        val newName = reader.nextName()
                        if (newName == "classificationValue") {
                            rating = readItemToString(reader)
                        } else {
                            reader.skipValue()
                        }
                    }
                    reader.endObject()
                } else if (name == "classificationYear") {
                    year = reader.nextString()
                } else if (name == "category") {
                    category = readItemToString(reader)
                } else if (name == "classificationYear") {
                    year = reader.nextString()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        cdePoint.addRNAG(RNAG(element, rating, activity, category, year.toInt()))
    }

    fun readItemToString(reader: JsonReader): String {
        var item = ""
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "label") {
                    item = reader.nextString()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return item
    }
}