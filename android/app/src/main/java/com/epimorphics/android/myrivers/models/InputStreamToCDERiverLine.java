package com.epimorphics.android.myrivers.models;

import com.epimorphics.android.myrivers.data.CDEPoint;
import com.epimorphics.android.myrivers.helpers.GeoJsonParser;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by mihajlo on 24/07/2017.
 */

public class InputStreamToCDERiverLine {

    private static final String TAG = "IN_STREAM_TO_RIVER_LINE";

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

    public void readJsonStream(InputStream in, CDEPoint cdePoint) throws IOException {
        JSONObject jsonObject = inStreamToJSONObject(in);
        Geometry geometry = GeoJsonParser.parseGeometry(jsonObject);
        GeoJsonFeature riverLine = new GeoJsonFeature(geometry, "Feature", null, null);
        cdePoint.setRiverLine(riverLine);
    }
}
