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
import static com.bitbusters.android.speproject.CDEPoint.DETAIL;
import static com.bitbusters.android.speproject.CDEPoint.ECOLOGICAL;
import static com.bitbusters.android.speproject.CDEPoint.GENERAL;
import static com.bitbusters.android.speproject.CDEPoint.HAZARDOUS_SUBSTANCES;
import static com.bitbusters.android.speproject.CDEPoint.HYDROMORPHOLOGICAL_ELEMENTS;
import static com.bitbusters.android.speproject.CDEPoint.OTHER_POLLUTANTS;
import static com.bitbusters.android.speproject.CDEPoint.OVERALL;
import static com.bitbusters.android.speproject.CDEPoint.PHYSICO_CHEMICAL_ELEMENTS;
import static com.bitbusters.android.speproject.CDEPoint.PRIORITY_SUBSTANCES;
import static com.bitbusters.android.speproject.CDEPoint.SPECIFIC_POLLUTANTS;
import static com.bitbusters.android.speproject.CDEPoint.SUPPORTING_ELEMENTS;

/**
 * Created by cp153 on 06/12/2016.
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
                        if (!isClassified(cdePoint, group)) {
                            readMessage(cdePoint, reader, group);
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
        addItem(cdePoint, item, group, value, certainty, year);
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

    private void addItem(CDEPoint cdePoint, String item, String group, String value,
                         String certainty, String year) {
        if(certainty.equals("No Information")) {
            certainty = "No Info";
        }
        switch(group) {
            case(GENERAL):
                if((item.equals(ECOLOGICAL) && !cdePoint.getClassificationHashMap(GENERAL).containsKey(ECOLOGICAL)) ||
                        (item.equals(CHEMICAL) && !cdePoint.getClassificationHashMap(GENERAL).containsKey(CHEMICAL)) ||
                        (item.equals(OVERALL) && !cdePoint.getClassificationHashMap(GENERAL).containsKey(OVERALL))) {
                    cdePoint.getClassificationHashMap(GENERAL).put(item, new Classification(value, certainty, year));
                }
                break;
            case(DETAIL):
                if((item.equals(SUPPORTING_ELEMENTS) && !cdePoint.getClassificationHashMap(ECOLOGICAL).containsKey(SUPPORTING_ELEMENTS)) ||
                        (item.equals(BIOLOGICAL_ELEMENTS) && !cdePoint.getClassificationHashMap(ECOLOGICAL).containsKey(BIOLOGICAL_ELEMENTS)) ||
                        (item.equals(HYDROMORPHOLOGICAL_ELEMENTS) && !cdePoint.getClassificationHashMap(ECOLOGICAL).containsKey(HYDROMORPHOLOGICAL_ELEMENTS)) ||
                        (item.equals(PHYSICO_CHEMICAL_ELEMENTS) && !cdePoint.getClassificationHashMap(ECOLOGICAL).containsKey(PHYSICO_CHEMICAL_ELEMENTS)) ||
                        (item.equals(SPECIFIC_POLLUTANTS) && !cdePoint.getClassificationHashMap(ECOLOGICAL).containsKey(SPECIFIC_POLLUTANTS))) {
                    cdePoint.getClassificationHashMap(ECOLOGICAL).put(item, new Classification(value, certainty, year));
                } else if((item.equals(PRIORITY_SUBSTANCES) && !cdePoint.getClassificationHashMap(CHEMICAL).containsKey(PRIORITY_SUBSTANCES)) ||
                        (item.equals(OTHER_POLLUTANTS) && !cdePoint.getClassificationHashMap(CHEMICAL).containsKey(OTHER_POLLUTANTS)) ||
                        (item.equals(HAZARDOUS_SUBSTANCES) && !cdePoint.getClassificationHashMap(CHEMICAL).containsKey(HAZARDOUS_SUBSTANCES))) {
                    cdePoint.getClassificationHashMap(CHEMICAL).put(item, new Classification(value, certainty, year));
                }
                break;
        }
    }

    private boolean isClassified(CDEPoint cdePoint, String group) {
        boolean result = false;
        switch(group) {
            case(DETAIL):
                result = cdePoint.getClassificationHashMap(ECOLOGICAL).containsKey(SUPPORTING_ELEMENTS)
                        && cdePoint.getClassificationHashMap(ECOLOGICAL).containsKey(BIOLOGICAL_ELEMENTS)
                        && cdePoint.getClassificationHashMap(ECOLOGICAL).containsKey(HYDROMORPHOLOGICAL_ELEMENTS)
                        && cdePoint.getClassificationHashMap(ECOLOGICAL).containsKey(PHYSICO_CHEMICAL_ELEMENTS)
                        && cdePoint.getClassificationHashMap(ECOLOGICAL).containsKey(SPECIFIC_POLLUTANTS)
                        && cdePoint.getClassificationHashMap(CHEMICAL).containsKey(PRIORITY_SUBSTANCES)
                        && cdePoint.getClassificationHashMap(CHEMICAL).containsKey(OTHER_POLLUTANTS)
                        && cdePoint.getClassificationHashMap(CHEMICAL).containsKey(HAZARDOUS_SUBSTANCES);
                break;
            case(OVERALL):
                result = cdePoint.getClassificationHashMap(GENERAL).containsKey(ECOLOGICAL)
                        && cdePoint.getClassificationHashMap(GENERAL).containsKey(CHEMICAL)
                        && cdePoint.getClassificationHashMap(GENERAL).containsKey(OVERALL);
                break;
        }
        return result;
    }

}
