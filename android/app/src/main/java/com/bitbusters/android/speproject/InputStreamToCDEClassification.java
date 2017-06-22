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

public class InputStreamToCDEClassification {

    public void readJsonStream(CDEPoint cdePoint, InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.setLenient(true);
        try {
            readMessagesArray(cdePoint, reader);
        } finally {
            reader.close();
        }
    }

    public void readMessagesArray(CDEPoint cdePoint, JsonReader reader) throws IOException {
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("items")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        if (!cdePoint.isClassified()) {
                            readMessage(cdePoint, reader);
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

    public void readMessage(CDEPoint cdePoint, JsonReader reader) throws IOException {
        String item = "";
        String certainty = "";
        String value = "";
        String year = "";
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("classificationCertainty")) {
                    certainty = readItemToString(reader);
                } else if (name.equals("classificationItem")) {
                    item = readItemToString(reader);
                } else if (name.equals("classificationValue")) {
                    value = readItemToString(reader);
                } else if (name.equals("classificationYear")) {
                    year = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(item.equals(CDEPoint.ECOLOGICAL) && !cdePoint.getClassificationHashMap().containsKey(CDEPoint.ECOLOGICAL) ||
                item.equals(CDEPoint.CHEMICAL) && !cdePoint.getClassificationHashMap().containsKey(CDEPoint.CHEMICAL) ||
                item.equals(CDEPoint.OVERALL) && !cdePoint.getClassificationHashMap().containsKey(CDEPoint.OVERALL)) {
            cdePoint.getClassificationHashMap().put(item, new Classification(value, certainty, year));
        }
    }

    public String readItemToString(JsonReader reader) {
        String item = "";
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("label")) {
                    item = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

}
