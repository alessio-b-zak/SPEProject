package com.bitbusters.android.speproject;

import android.util.JsonReader;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cp153 on 06/12/2016.
 */

public class InputStreamToWIMSMeasurements {
    public void readJsonStream(WIMSPoint wimsPoint, InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readMessagesArray(wimsPoint, reader);
        } finally {
            reader.close();
        }
    }

    public void readMessagesArray(WIMSPoint wimsPoint, JsonReader reader) throws IOException {
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("items")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        if (!wimsPoint.measurementsPopulated()) {
                            readMessage(wimsPoint, reader);
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
    }

    public void readMessage(WIMSPoint wimsPoint, JsonReader reader) throws IOException {
        String determinand = null;
        String dateTimeString = null;
        DateTime dateTime = null;
        Date date = null;
        Double result = 0.0;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("determinand")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("label")) {
                            determinand = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                } else if (name.equals("result")) {
                    result = reader.nextDouble();
                } else if (name.equals("sample")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("sampleDateTime")) {
                            dateTimeString = reader.nextString();
                            dateTime = new DateTime(dateTimeString);
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

        wimsPoint.getMeasurementList().add(new Measurement(determinand, result, dateTime));
    }
}
