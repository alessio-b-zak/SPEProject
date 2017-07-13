package com.bitbusters.android.speproject;

import android.util.Log;

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
        JSONObject jsonObject = inStreamToJSONObject(in);
        List<CDEPoint> messages = new ArrayList<CDEPoint>();
        int i = 0;
        try {
            while (i < jsonObject.getJSONArray("features").length()) {
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

    private static JSONObject inStreamToJSONObject(InputStream in) {
        JSONObject jsonObject = new JSONObject();
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            jsonObject = new JSONObject(responseStrBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
