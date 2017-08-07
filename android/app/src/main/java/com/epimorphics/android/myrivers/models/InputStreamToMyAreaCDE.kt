package com.epimorphics.android.myrivers.models

import android.util.JsonReader
import com.epimorphics.android.myrivers.data.Characteristic
import com.epimorphics.android.myrivers.data.MyArea
import com.epimorphics.android.myrivers.helpers.InputStreamHelper
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by mihajlo on 04/07/17.
 */
class InputStreamToMyAreaCDE : InputStreamHelper() {

    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream, myArea: MyArea) {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        reader.use {
            readMessagesArray(reader, myArea)
        }
    }

    @Throws(IOException::class)
    fun readMessagesArray(reader: JsonReader, myArea: MyArea) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "items") {
                reader.beginArray()
                while (reader.hasNext()) {
                    readMessage(reader, myArea)
                }
                reader.endArray()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
    }

    @Throws(IOException::class)
    fun readMessage(reader: JsonReader, myArea: MyArea) {
        val characteristicList = arrayListOf<Characteristic>()

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "label" -> myArea.waterbody = reader.nextString()
                "isVersionOf" -> myArea.operationalCatchment = readVersionOf(reader, characteristicList)
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        myArea.characteristicList = characteristicList;
    }

    @Throws(IOException::class)
    fun readVersionOf(reader: JsonReader, characteristicList: ArrayList<Characteristic>): String {
        var operationalCatchment = ""

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "characteristic" -> readCharacteristics(reader, characteristicList)
                "inOperationalCatchment" -> operationalCatchment = readItemToString(reader, "@id")
                else -> reader.skipValue()
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
        if (unitLabel[0] != "ha") {
            characteristicList.add(Characteristic(unitLabel[0], unitLabel[1], value))
        }
    }

    @Throws(IOException::class)
    fun readUnitLabel(reader: JsonReader): ArrayList<String> {
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