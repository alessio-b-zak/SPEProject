package com.epimorphics.android.myrivers.models

import android.util.JsonReader
import com.epimorphics.android.myrivers.data.Characteristic
import com.epimorphics.android.myrivers.data.MyArea
import com.epimorphics.android.myrivers.helpers.InputStreamHelper
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Consumes an InputStream and populates relevant fields of MyArea relating to the nearest waterbody
 *
 * @see MyArea
 * @see Characteristic
 */
class InputStreamToMyAreaCDE : InputStreamHelper() {

    /**
     * Converts InputStream to JsonReader and consumes it.
     *
     * @param inputStream InputStream to be consumed
     * @param myArea an object which is to be populated with waterbody data
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readJsonStream(inputStream: InputStream, myArea: MyArea) {
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))
        reader.isLenient = true
        reader.use {
            readMessagesArray(reader, myArea)
        }
    }

    /**
     * Focuses on the array of objects that are to be converted to Classifications and parses
     * them one by one.
     *
     * @param reader JsonReader to be consumed
     * @param myArea an object which is to be populated with waterbody data
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readMessagesArray(reader: JsonReader, myArea: MyArea) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == "items") {
                reader.beginArray()
                while (reader.hasNext()) {
                    readWaterbody(reader, myArea)
                }
                reader.endArray()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()
    }

    /**
     * Reads in waterbody name and continues to parse operationalCatchment and characteristicList
     *
     * @param reader JsonReader to be consumed
     * @param myArea an object which is to be populated with waterbody data
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readWaterbody(reader: JsonReader, myArea: MyArea) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "label" -> myArea.waterbody = reader.nextString()
                "isVersionOf" -> readOperationalCatchment(reader, myArea)
                else -> reader.skipValue()
            }
        }
        reader.endObject()
    }

    /**
     * Reads in waterbody operationalCatchment and continues to parse characteristicList
     *
     * @param reader JsonReader to be consumed
     * @param myArea an object which is to be populated with waterbody data
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readOperationalCatchment(reader: JsonReader, myArea: MyArea) {
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
                "characteristic" -> readCharacteristics(reader, myArea)
                "inOperationalCatchment" -> myArea.operationalCatchment = readItemToString(reader, "@id")
                else -> reader.skipValue()
            }
        }
        reader.endObject()
    }

    /**
     * Reads a characteristicList from the JsonObject
     *
     * @param reader JsonReader to be consumed
     * @param myArea an object which is to be populated with waterbody data
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readCharacteristics(reader: JsonReader, myArea: MyArea) {
        val characteristicList = arrayListOf<Characteristic>()

        reader.beginArray()
        while (reader.hasNext()) {
            readSingleCharacteristic(reader, characteristicList)
        }
        reader.endArray()

        myArea.characteristicList = characteristicList
    }

    /**
     * Reads a single characteristic from the JsonObject and adds it to the characteristicList
     *
     * @param reader JsonReader to be consumed
     * @param characteristicList a list to be populated with individual characteristics
     *
     * @throws IOException
     */
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

    /**
     * Reads a single characteristic unit and label from the JsonObject and returns it
     *
     * @param reader JsonReader to be consumed
     * @return ArrayList of String containing unit and label of a currently parsed characteristic
     *
     * @throws IOException
     */
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