package com.bitbusters.android.speproject;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cp153 on 06/12/2016.
 */

public class InputStreamToWIMSPoint {
    public List<WIMSPoint> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<WIMSPoint> readMessagesArray(JsonReader reader) throws IOException {
        List<WIMSPoint> messages = new ArrayList<WIMSPoint>();
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                messages.add(readMessage(reader));
            }
            reader.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }

    public WIMSPoint readMessage(JsonReader reader) throws IOException {
        String id = null;
        double latitude = 0.0, longitude = 0.0;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id")) {
                    id = reader.nextString();
                } else if (name.equals("latitude")) {
                    latitude = reader.nextDouble();
                } else if (name.equals("longitude")) {
                    longitude = reader.nextDouble();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new WIMSPoint(id, latitude, longitude);
    }
}
