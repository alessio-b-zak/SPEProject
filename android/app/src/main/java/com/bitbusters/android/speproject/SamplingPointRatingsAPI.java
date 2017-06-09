package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SamplingPointRatingsAPI extends AsyncTask<SamplingPoint, Void, SamplingPoint> {

    private static final String TAG = "SAMPLING_POINT_RATINGS";
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
                    .encodedAuthority("139.59.184.70:8080")
                    //.encodedAuthority("172.23.215.243:3000")
                    .appendPath("getClassification")
                    .appendPath(String.valueOf(easting))
                    .appendPath(String.valueOf(northing));
            String myUrl = builder.build().toString();
            Log.i(TAG, myUrl);
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.i(TAG, "Url is: " + url);
            Log.i(TAG, "The response is: " + response);
            InputStream inputStream = null;
            inputStream = conn.getInputStream();
            InputStreamUpdateRatings inputStreamUpdateRatings = new InputStreamUpdateRatings();
            InputStreamUpdateRatings.readJsonStream(samplingPoint, inputStream);
            Log.i(TAG, "The result is: Chemical: " + samplingPoint.getChemicalRating() + " Ecological: " + samplingPoint.getEcologicalRating());

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
