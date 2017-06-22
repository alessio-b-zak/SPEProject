package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CDEPointRatingsAPI extends AsyncTask<CDEPoint, Void, CDEPoint> {

    private static final String TAG = "CDE_POINT_RATINGS";
    private SPDataFragment mSPDataFragment;

    public CDEPointRatingsAPI() {

    }

    @Override
    protected CDEPoint doInBackground(CDEPoint...params) {
        CDEPoint cdePoint = params[0];
        String waterbodyId = cdePoint.getWaterbodyId();
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("ea-cde-pub.epimorphics.net")
                    .appendPath("catchment-planning")
                    .appendPath("data")
                    .appendPath("classification.json")
                    .appendQueryParameter("waterBody", waterbodyId)
                    .appendQueryParameter("_sort", "-classificationYear")
                    .appendQueryParameter("_sort", "-cycle");
            String myUrl = builder.build().toString();
//            Log.i(TAG, myUrl);
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
//            inputStream = conn.getInputStream();
            InputStreamToCDEClassification inputStreamToCDEClassification = new InputStreamToCDEClassification();
            inputStreamToCDEClassification.readJsonStream(cdePoint, inputStream);
            cdePoint.printClassification();
//            Log.i(TAG, "The result is: Chemical: " + samplingPoint.getChemicalRating() + " Ecological: " + samplingPoint.getEcologicalRating());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cdePoint;
    }

    @Override
    protected void onPostExecute(CDEPoint cdePoint) {
//        mSPDataFragment.setChemBioText(samplingPoint);
    }

}
