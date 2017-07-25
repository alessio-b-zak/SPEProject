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
class InputStreamToWIMSMeasurements: InputStreamHelper() {

    @Throws(IOException::class)
    fun readJsonStream(wimsPoint: WIMSPoint, `in`: InputStream) {
        val reader = JsonReader(InputStreamReader(`in`, "UTF-8"))
        reader.use {
            readMessagesArray(wimsPoint, reader)
        }
    }

    @Throws(IOException::class)
    fun readMessagesArray(wimsPoint: WIMSPoint, reader: JsonReader) {
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
    }

    @Throws(IOException::class)
    fun readMessage(wimsPoint: WIMSPoint, reader: JsonReader) {
        var determinand = ""
        var dateTimeString = ""
        var date = ""
        var unit = ""
        var label = ""
        var result: Double = 0.0

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
                        unit = readItemToString(reader)
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
                        label = readItemToString(reader);
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

        if(wimsPoint.measurementMap.containsKey(determinand)) {
            var measurementList = wimsPoint.measurementMap[determinand]
            if(measurementList != null) {
                measurementList.add(Measurement(unit, result, date))
                wimsPoint.label = label
            } else {
                measurementList = arrayListOf<Measurement>(Measurement(unit, result, date))
                wimsPoint.measurementMap.put(determinand, measurementList)
                wimsPoint.label = label
            }
        } else {
            val measurementList = arrayListOf<Measurement>(Measurement(unit, result, date))
            wimsPoint.measurementMap.put(determinand, measurementList)
            wimsPoint.label = label
        }
    }
}