package com.bitbusters.android.speproject;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.geojson.GeoJsonFeature;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cp153 on 06/12/2016.
 */

public class InputStreamToCDEPoint {

    private static final String TAG = "IN_STREAM_TO_CDE";

    private CoordinateSystemConverter coordinateSystemConverter;

    InputStreamToCDEPoint() {
        coordinateSystemConverter = new CoordinateSystemConverter();
    }

    public List<CDEPoint> readJsonStream(InputStream in) throws IOException {
        JSONObject jsonObject = inputStreamToJSONObject(in);
        List<CDEPoint> messages = new ArrayList<CDEPoint>();
        int i = 0;
        try {
            while (i < jsonObject.getJSONArray("features").length()) {
//                Log.i("FIND ME", "
                JSONObject feature = jsonObject.getJSONArray("features").getJSONObject(i);
                GeoJsonFeature geoJsonFeature = GeoJsonParser.parseFeature(feature);
                JSONObject properties = feature.getJSONObject("properties");
                String waterbodyId = properties.getString("waterBodyNotation");
                String label = properties.getString("label");
                String ngr = properties.getString("ngr");

                LatLng location = coordinateSystemConverter.convertNgrToLatLng(ngr);

                messages.add(new CDEPoint(waterbodyId, label, location.latitude, location.longitude, geoJsonFeature));
//                Log.i(TAG,"WaterbodyID : " + waterbodyId);
//                Log.i(TAG,"label : " + label);
//                Log.i(TAG,"location : " + location.toString());
//                Log.i(TAG,"feature : " + geoJsonFeature.toString());
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messages;
    }

//    public List<CDEPoint> readMessagesArray(JsonReader reader) throws IOException {
//        List<CDEPoint> messages = new ArrayList<CDEPoint>();
//        int i = 0;
//        try {
//            reader.beginObject();
//            while (reader.hasNext()) {
//                String name = reader.nextName();
//                if (name.equals("features")) {
//                    reader.beginArray();
//                    while (reader.hasNext()) {
//                        messages.add(readMessage(reader, i));
//                    }
//                    reader.endArray();
//                } else {
//                    reader.skipValue();
//                }
//            }
//            reader.endObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return messages;
//    }
//
//    public CDEPoint readMessage(JsonReader reader, int i) throws IOException {
//        String waterbodyId = null;
//        String label = null;
//        String type = null;
//        String ngr = null;
//        GeoJsonFeature geoJsonFeature = null;
//        double latitude = 0.0, longitude = 0.0;
//        try {
//            JSONObject feature = jsonObject.getJSONArray("features")
//                    .getJSONObject(i);
//            geoJsonFeature = GeoJsonParser.parseFeature(feature);
//            Log.i("FIND ME", geoJsonFeature.toString());
//            reader.beginObject();
//            while (reader.hasNext()) {
//                String name = reader.nextName();
//                if (name.equals("properties")) {
//                    reader.beginObject();
//                    while (reader.hasNext()) {
//                        name = reader.nextName();
//                        if (name.equals("label")) {
//                            label = reader.nextString();
//                        } else if (name.equals("ngr")) {
//                            ngr = reader.nextString();
//                            //                } else if (name.equals("type")) {
//                            //                    type = reader.nextString();
//                        } else if (name.equals("waterBodyNotation")) {
//                            waterbodyId = reader.nextString();
//                        } else {
//                            reader.skipValue();
//                        }
//                    }
//                    reader.endObject();
//                }
//            }
//            reader.endObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        LatLng location = coordinateSystemConverter.convertNgrToLatLng(ngr);
//
//        return new CDEPoint(waterbodyId, label, location.latitude, location.longitude, geoJsonFeature);
//    }

    private static JSONObject inputStreamToJSONObject(InputStream inputStream) {
        String result = "";

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject res = null;
        try {
            res = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
//
//    if (name.equals("geometry")) {
//        reader.beginObject();
//        while (reader.hasNext()) {
//            name = reader.nextName();
//            if (name.equals("type")) {
//                type = reader.nextString();
//            } else if (name.equals("coordinates")) {
//                reader.beginArray();
//                while(reader.hasNext()) {
//                    List<LatLng> latLngList = new ArrayList<>();
//                    reader.beginArray();
//                    reader.beginArray();
//                    while (reader.hasNext()) {
//                        reader.beginArray();
//                        longitude = reader.nextDouble();
//                        latitude = reader.nextDouble();
//                        GeoJsonPolygon polygon = new GeoJsonPolygon()
//                        reader.endArray();
//                    }
//                    reader.endArray();
//                    reader.endArray();
//                }
//                reader.endArray();
//                coordinates = reader.nextString();
//            } else {
//                reader.skipValue();
//            }
//        }
//        reader.endObject();
//    } else
}
