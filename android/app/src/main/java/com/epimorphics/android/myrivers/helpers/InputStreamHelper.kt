package com.epimorphics.android.myrivers.helpers

import android.util.JsonReader
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Helper class providing base functions used by most input stream converters
 */
abstract class InputStreamHelper {
    /**
     * Reads a value corresponding to a given key inside a next Json Object
     *
     * @param reader JsonReader
     * @param key key
     *
     * @return value as a string corresponding to the given key
     */
    fun readItemToString(reader: JsonReader, key: String = "label"): String {
        var item = ""

        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            if (name == key) {
                item = reader.nextString()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()

        return item
    }
}