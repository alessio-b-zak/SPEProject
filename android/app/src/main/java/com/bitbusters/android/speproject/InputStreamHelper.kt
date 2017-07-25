package com.bitbusters.android.speproject

import android.util.JsonReader

/**
 * Created by mihajlo on 25/07/17.
 */
abstract class InputStreamHelper {
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