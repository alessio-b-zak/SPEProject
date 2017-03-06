package com.bitbusters.android.speproject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import static com.google.android.gms.internal.zznu.is;

public class SamplingPointRatingsAPI extends AsyncTask<SamplingPoint, Void, SamplingPoint> {

    private static final String DEBUG_TAG = "SAMPLING_POINT_RATINGS";
    private SPDataFragment mSPDataFragment;

    public SamplingPointRatingsAPI(SPDataFragment context) {
        this.mSPDataFragment = context;
    }

    @Override
    protected SamplingPoint doInBackground(SamplingPoint...params) {
        SamplingPoint samplingPoint = params[0];
        Integer easting = samplingPoint.getEasting();
        Integer northing = samplingPoint.getNorthing();
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("139.59.184.70:3000")
                    .appendPath("getClassification")
                    .appendPath(String.valueOf(easting))
                    .appendPath(String.valueOf(northing));
            String myUrl = builder.build().toString();
            Log.d(DEBUG_TAG, myUrl);
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "Url is: " + url);
            Log.d(DEBUG_TAG, "The response is: " + response);
            InputStream inputStream = null;
            inputStream = conn.getInputStream();
            InputStreamUpdateRatings inputStreamUpdateRatings = new InputStreamUpdateRatings();
            InputStreamUpdateRatings.readJsonStream(samplingPoint, inputStream);
            Log.d(DEBUG_TAG, "The result is: Chemical: " + samplingPoint.getChemicalRating() + " Ecological: " + samplingPoint.getEcologicalRating());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return samplingPoint;
    }

    @Override
    protected void onPostExecute(SamplingPoint samplingPoint) {
        mSPDataFragment.setChemBioText(samplingPoint);
    }

}
