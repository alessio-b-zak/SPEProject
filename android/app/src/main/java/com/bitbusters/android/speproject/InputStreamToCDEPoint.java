package com.bitbusters.android.speproject;

import android.util.JsonReader;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cp153 on 06/12/2016.
 */

public class InputStreamToCDEPoint {

    private NGRtoWGS84Converter ngRtoWGS84Converter;

    InputStreamToCDEPoint() {
        ngRtoWGS84Converter = new NGRtoWGS84Converter();
    }

    public List<CDEPoint> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.setLenient(true);
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<CDEPoint> readMessagesArray(JsonReader reader) throws IOException {
        List<CDEPoint> messages = new ArrayList<CDEPoint>();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("items")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        messages.add(readMessage(reader));
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
        return messages;
    }

    public CDEPoint readMessage(JsonReader reader) throws IOException {
        String waterbodyId = null;
        String label = null;
//        String type = null;
        String ngr = null;
        double latitude = 0.0, longitude = 0.0;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("label")) {
                    label = reader.nextString();
                } else if (name.equals("ngr")) {
                    ngr = reader.nextString();
//                } else if (name.equals("type")) {
//                    type = reader.nextString();
                } else if (name.equals("waterBodyNotation")) {
                    waterbodyId = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LatLng location = ngRtoWGS84Converter.convert(ngr);

        return new CDEPoint(waterbodyId, label, location.latitude, location.longitude);
    }
}
