package com.bitbusters.android.speproject

import android.util.JsonReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by mihajlo on 04/07/17.
 */
class InputStreamToWIMSMeasurements {

    @Throws(IOException::class)
    fun readJsonStream(wimsPoint: WIMSPoint, `in`: InputStream) {
        val reader = JsonReader(InputStreamReader(`in`, "UTF-8"))
        try {
            readMessagesArray(wimsPoint, reader)
        } finally {
            reader.close()
        }
    }

    @Throws(IOException::class)
    fun readMessagesArray(wimsPoint: WIMSPoint, reader: JsonReader) {
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                val name = reader.nextName()
                if (name == "items") {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        readMessage(wimsPoint, reader)
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
    fun readMessage(wimsPoint: WIMSPoint, reader: JsonReader) {
        var determinand = ""
        var dateTimeString = ""
        var date = ""
        var unit = ""
        var label = ""
        var result: Double = 0.0
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                var name = reader.nextName()
                if (name == "determinand") {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        name = reader.nextName()
                        if (name == "label") {
                            determinand = reader.nextString()
                        } else if (name == "unit") {
                            reader.beginObject()
                            while (reader.hasNext()) {
                                name = reader.nextName()
                                if (name == "label") {
                                    unit = reader.nextString()
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
                } else if (name == "result") {
                    result = reader.nextDouble()
                } else if (name == "sample") {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        name = reader.nextName()
                        if (name == "sampleDateTime") {
                            dateTimeString = reader.nextString()
                            date = dateTimeString.substring(0, 10);
                        } else if (name == "samplingPoint") {
                            reader.beginObject()
                            while (reader.hasNext()) {
                                name = reader.nextName()
                                if (name == "label") {
                                    label = reader.nextString()
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
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if(!wimsPoint.measurementMap.containsKey(determinand)) {
            wimsPoint.measurementMap.put(determinand, Measurement(unit, result, date))
            wimsPoint.label = label
        }
    }
}