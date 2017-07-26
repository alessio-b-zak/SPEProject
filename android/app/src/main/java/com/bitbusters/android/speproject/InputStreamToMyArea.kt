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
class InputStreamToMyArea: InputStreamHelper() {

    private lateinit var myArea: MyArea

    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream): MyArea {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        reader.use {
            readMessagesArray(reader)
        }
        return myArea
    }

    @Throws(IOException::class)
    fun readMessagesArray(reader: JsonReader) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "items") {
                reader.beginArray()
                while (reader.hasNext()) {
                    readMessage(reader)
                }
                reader.endArray()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
    }

    @Throws(IOException::class)
    fun readMessage(reader: JsonReader) {
        var waterbody = ""
        var operationalCatchment = ""
        val characteristicList = arrayListOf<Characteristic>()

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when(name){
                "label"       -> waterbody = reader.nextString()
                "isVersionOf" -> operationalCatchment = readVersionOf(reader, characteristicList)
                else          -> reader.skipValue()
            }
        }
        reader.endObject()

        myArea = MyArea(waterbody, operationalCatchment, characteristicList)
    }

    @Throws(IOException::class)
    fun readVersionOf(reader: JsonReader, characteristicList: ArrayList<Characteristic>): String {
        var operationalCatchment = ""

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when(name){
                "characteristic"         -> readCharacteristics(reader, characteristicList)
                "inOperationalCatchment" -> operationalCatchment = readItemToString(reader, "@id")
                else                     -> reader.skipValue()
            }
        }
        reader.endObject()

        return operationalCatchment
    }

    @Throws(IOException::class)
    fun readCharacteristics(reader: JsonReader, characteristicList: ArrayList<Characteristic>) {
        reader.beginArray()
        while (reader.hasNext()) {
            readSingleCharacteristic(reader, characteristicList)
        }
        reader.endArray()
    }

    @Throws(IOException::class)
    fun readSingleCharacteristic(reader: JsonReader, characteristicList: ArrayList<Characteristic>) {
        var unitLabel = arrayListOf<String>()
        var value = 0.0

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "characteristicID" -> unitLabel = readUnitLabel(reader)
                "characteristicValue" -> value = reader.nextDouble()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        characteristicList.add(Characteristic(unitLabel[0], unitLabel[1], value))
    }

    @Throws(IOException::class)
    fun readUnitLabel(reader: JsonReader): ArrayList<String>{
        var unit = ""
        var label = ""

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "characteristicUnit" -> unit = reader.nextString()
                "label" -> label = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return arrayListOf<String>(unit, label)
    }
}