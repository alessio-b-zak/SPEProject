package com.epimorphics.android.myrivers.models

import android.util.JsonReader
import com.epimorphics.android.myrivers.data.Measurement
import com.epimorphics.android.myrivers.data.WIMSPoint
import com.epimorphics.android.myrivers.helpers.InputStreamHelper
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Consumes an InputStream and converts it to a list of Measurements
 *
 * @see WIMSPoint
 * @see Measurement
 */
class InputStreamToWIMSMeasurements : InputStreamHelper() {

    /**
     * Converts InputStream to JsonReader and consumes it.
     *
     * @param wimsPoint WIMSPoint to which parsed Measurements belong
     * @param inputStream InputStream to be consumed
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readJsonStream(wimsPoint: WIMSPoint, inputStream: InputStream) {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.use {
            readMessagesArray(wimsPoint, reader)
        }
    }

    /**
     * Focuses on the array of objects that are to be converted to Measurements and parses
     * them one by one.
     *
     * @param wimsPoint WIMSPoint to which parsed Measurements belong
     * @param reader JsonReader to be consumed
     *
     * @throws IOException
     */
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

    /**
     * Converts single JsonObject to a Measurement and adds it to the given WIMSPoint's measurementMap.
     *
     * @param wimsPoint WIMSPoint to which parsed Measurement belongs
     * @param reader JsonReader to be consumed
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessage(wimsPoint: WIMSPoint, reader: JsonReader) {
        var determinand = ""
        var dateTimeString = ""
        var date = ""
        var unit = ""
        var label = ""
        var descriptor = ""
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
                    } else if (name == "definition") {
                        descriptor = reader.nextString()
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

        // If wimsPoint contains parsed determinand a new measurement is added to the array list
        // of measurements for a given determinand. Otherwise a new measurement list is created and
        // populated with currently parsed measurement.
        if (wimsPoint.measurementMap.containsKey(determinand)) {
            var measurementList = wimsPoint.measurementMap[determinand]
            if (measurementList != null) {
                measurementList.add(Measurement(unit, result, descriptor, date))
                wimsPoint.label = label
            } else {
                measurementList = arrayListOf<Measurement>(Measurement(unit, result, descriptor, date))
                wimsPoint.measurementMap.put(determinand, measurementList)
                wimsPoint.label = label
            }
        } else {
            val measurementList = arrayListOf<Measurement>(Measurement(unit, result, descriptor, date))
            wimsPoint.measurementMap.put(determinand, measurementList)
            wimsPoint.label = label
        }
    }
}