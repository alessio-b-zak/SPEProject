package com.epimorphics.android.myrivers.models;

import com.epimorphics.android.myrivers.data.CDEPoint;
import com.epimorphics.android.myrivers.helpers.GeoJsonParser;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Consumes an InputStream and converts it to a GeoJsonFeature
 *
 * @see CDEPoint
 * @see GeoJsonFeature
 */
public class InputStreamToCDERiverLine {

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
     * Converts an InputStream to the GeoJsonFeature riverLine and sets it to the given CDEPoint
     *
     * @param inputStream InputStream to be converted
     * @param cdePoint CDEPoint to which a converted river line belongs
     * @throws IOException if InputStream not convertible to JSONObject
     * @throws JSONException if JSONObject not accessed properly
     *
     * @see CDEPoint
     * @see GeoJsonFeature
     */
    public void readJsonStream(InputStream inputStream, CDEPoint cdePoint) throws IOException, JSONException {
        JSONObject jsonObject = inStreamToJSONObject(inputStream);
        Geometry geometry = GeoJsonParser.parseGeometry(jsonObject);
        GeoJsonFeature riverLine = new GeoJsonFeature(geometry, "Feature", null, null);
        cdePoint.setRiverLine(riverLine);
    }
}
