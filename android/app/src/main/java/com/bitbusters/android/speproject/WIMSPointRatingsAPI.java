package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WIMSPointRatingsAPI extends AsyncTask<WIMSPoint, Void, WIMSPoint> {

    private static final String TAG = "SAMPLING_POINT_RATINGS";
    private WIMSDataFragment mWIMSDataFragment;

    public WIMSPointRatingsAPI(WIMSDataFragment context) {
        this.mWIMSDataFragment = context;
    }

    @Override
    protected WIMSPoint doInBackground(WIMSPoint...params) {
        WIMSPoint wimsPoint = params[0];
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("environment.data.gov.uk")
                    .appendPath("water-quality")
                    .appendPath("data")
                    .appendPath("measurement")
                    .appendQueryParameter("samplingPoint", wimsPoint.getId())
                    .appendQueryParameter("_sort", "-sample");
            String myUrl = builder.build().toString();
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

            InputStream inputStream = conn.getInputStream();
            InputStreamToWIMSMeasurements inputStreamToWIMSMeasurements = new InputStreamToWIMSMeasurements();
            inputStreamToWIMSMeasurements.readJsonStream(wimsPoint, inputStream);

//            for(Measurement m : wimsPoint.getMeasurementList()) {
//                Log.i(TAG, "Measurement: " + m.getDeterminand() + " Result: " + m.getResult() + " Date: " + m.getDateTime().toLocalDate());
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return wimsPoint;
    }

    @Override
    protected void onPostExecute(WIMSPoint wimsPoint) {
        mWIMSDataFragment.setMeasurementsText(wimsPoint);
    }

}
