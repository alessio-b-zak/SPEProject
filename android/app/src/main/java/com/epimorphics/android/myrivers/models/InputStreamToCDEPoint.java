package com.epimorphics.android.myrivers.models;

import com.epimorphics.android.myrivers.data.CDEPoint;
import com.epimorphics.android.myrivers.helpers.CoordinateSystemConverter;
import com.epimorphics.android.myrivers.helpers.GeoJsonParser;
import com.epimorphics.android.myrivers.helpers.InputStreamHelper;
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
 * Consumes an InputStream and converts it to a List<CDEPoint>
 *
 * @see CDEPoint
 */
public class InputStreamToCDEPoint extends InputStreamHelper {

    /**
     * Converts InputStream to JsonObject
     *
     * @param inputStream InputStream to be converted
     * @return JSONObject corresponding to the given InputStream
     * @throws JSONException if JSONObject not convertible from StringBuilder
     * @throws IOException if InputStream not convertible to BufferReader
     */
    private static JSONObject inStreamToJSONObject(InputStream inputStream)
            throws IOException, JSONException {

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        return new JSONObject(responseStrBuilder.toString());
    }

    /**
     * Converts InputStream to a List<CDEPoint>
     *
     * @param inputStream InputStream to be converted
     * @return List<CDEPoint> resulting from conversion of inputStream
     * @throws IOException if InputStream not convertible to JSONObject
     * @throws JSONException if JSONObject not accessed properly
     */
    public List<CDEPoint> readJsonStream(InputStream inputStream) throws IOException, JSONException {
        JSONObject jsonObject = inStreamToJSONObject(inputStream);
        List<CDEPoint> messages = new ArrayList<CDEPoint>();
        int i = 0;

        while (i < jsonObject.getJSONArray("features").length()) {
            JSONObject feature = jsonObject.getJSONArray("features").getJSONObject(i);
            GeoJsonFeature geoJsonFeature = GeoJsonParser.parseFeature(feature);
            JSONObject properties = feature.getJSONObject("properties");
            String waterbodyId = properties.getString("waterBodyNotation");
            String label = properties.getString("label");

            messages.add(new CDEPoint(waterbodyId, label, geoJsonFeature));
            i++;
        }

        return messages;
    }
}
