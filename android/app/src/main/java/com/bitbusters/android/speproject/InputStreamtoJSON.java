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

public class InputStreamToJSON {
    public List<SamplingPoint> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<SamplingPoint> readMessagesArray(JsonReader reader) throws IOException {
        List<SamplingPoint> messages = new ArrayList<SamplingPoint>();
        try {
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("items")) {
                    System.out.println("Start items.");
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

    public SamplingPoint readMessage(JsonReader reader) throws IOException {
        String id = null;
        String samplingPointType = null;
        double latitude = 0.0, longitude = 0.0;
        try {
            reader.beginObject();
            System.out.println("sampling point Object began.");
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("@id")) {
                    System.out.println("Read id.");
                    id = reader.nextString();
                } else if (name.equals("lat")) {
                    System.out.println("Read lat.");
                    latitude = reader.nextDouble();
                } else if (name.equals("long")) {
                    System.out.println("Read long.");
                    longitude = reader.nextDouble();
                } else if (name.equals("samplingPointType")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("label")) {
                            System.out.println("Got label");
                            samplingPointType = reader.nextString();
                        } else {
                            reader.skipValue();
                        }

                    }
                    reader.endObject();
                    System.out.println("Read SamplingPointType.");
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SamplingPoint(id, latitude, longitude, samplingPointType);
    }
}
