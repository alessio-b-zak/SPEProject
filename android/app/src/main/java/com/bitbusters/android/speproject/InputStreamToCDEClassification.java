package com.bitbusters.android.speproject;

import android.util.JsonReader;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.bitbusters.android.speproject.CDEPoint.BIOLOGICAL_ELEMENTS;
import static com.bitbusters.android.speproject.CDEPoint.CHEMICAL;
import static com.bitbusters.android.speproject.CDEPoint.ECOLOGICAL;
import static com.bitbusters.android.speproject.CDEPoint.HAZARDOUS_SUBSTANCES;
import static com.bitbusters.android.speproject.CDEPoint.HYDROMORPHOLOGICAL_ELEMENTS;
import static com.bitbusters.android.speproject.CDEPoint.OTHER_POLLUTANTS;
import static com.bitbusters.android.speproject.CDEPoint.OVERALL;
import static com.bitbusters.android.speproject.CDEPoint.PHYSICO_CHEMICAL_ELEMENTS;
import static com.bitbusters.android.speproject.CDEPoint.PRIORITY_SUBSTANCES;
import static com.bitbusters.android.speproject.CDEPoint.SPECIFIC_POLLUTANTS;
import static com.bitbusters.android.speproject.CDEPoint.SUPPORTING_ELEMENTS;

/**
 * Created by mihajlo on 12/07/2017.
 */

public class InputStreamToCDEClassification {

    public void readJsonStream(CDEPoint cdePoint, InputStream in, String group) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.setLenient(true);
        try {
            readMessagesArray(cdePoint, reader, group);
        } finally {
            reader.close();
        }
    }

    public void readMessagesArray(CDEPoint cdePoint, JsonReader reader, String group) throws IOException {
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("items")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        readMessage(cdePoint, reader, group);
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

    public void readMessage(CDEPoint cdePoint, JsonReader reader, String group) throws IOException {
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

        if(certainty.equals("No Information")) {
            certainty = "No Info";
        }

        if(!cdePoint.getClassificationHashMap(group).containsKey(item)) {
            cdePoint.getClassificationHashMap(group).put(item, new Classification(value, certainty, year));
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
