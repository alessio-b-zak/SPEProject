package com.bitbusters.android.speproject;

import android.database.sqlite.SQLiteDatabase;
import android.util.JsonReader;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by cp153 on 06/12/2016.
 */

public class InputStreamToWIMSMeasurementsDatabase {

    private boolean populated = false;

    public String readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        String result = "";
        try {
            result = readMessagesArray(reader);
        } finally {
            reader.close();
        }
        return result;
    }

    public String readMessagesArray(JsonReader reader) throws IOException {
        String result = "";
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("items")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        if (!populated) {
                            result = readMessage(reader);
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String readMessage(JsonReader reader) throws IOException {
        String dateTimeString = null;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("sample")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("sampleDateTime")) {
                            dateTimeString = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        populated = true;

        dateTimeString = dateTimeString.substring(0,4);

        return dateTimeString;
    }
}
