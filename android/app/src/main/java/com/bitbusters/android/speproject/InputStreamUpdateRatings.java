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

public class InputStreamUpdateRatings {
    public static void readJsonStream(SamplingPoint samplingPoint, InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readMessage(samplingPoint, reader);
        } finally {
            reader.close();
        }
    }

    public static void readMessage(SamplingPoint samplingPoint, JsonReader reader) throws IOException {
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("Ecological")) {
                    samplingPoint.setEcologicalRating(reader.nextString());
                } else if (name.equals("Chemical")) {
                    samplingPoint.setChemicalRating(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
