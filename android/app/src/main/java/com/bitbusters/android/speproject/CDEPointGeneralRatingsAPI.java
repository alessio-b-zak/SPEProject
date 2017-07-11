package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class CDEPointGeneralRatingsAPI extends AsyncTask<CDEPoint, Void, CDEPoint> {

    private static final String TAG = "CDE_GENERAL_RATINGS";
    private CDEDataFragment mCDEDataFragment;

    public CDEPointGeneralRatingsAPI(CDEDataFragment context) {
        this.mCDEDataFragment = context;
    }

    @Override
    protected CDEPoint doInBackground(CDEPoint...params) {
        CDEPoint cdePoint = params[0];
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("ea-cde-pub.epimorphics.net")
                    .appendPath("catchment-planning")
                    .appendPath("data")
                    .appendPath("classification.json")
                    .appendQueryParameter("waterBody", cdePoint.getWaterbodyId())
                    .appendQueryParameter("classificationItem", "wbc_1")
                    .appendQueryParameter("classificationItem", "wbc_2")
                    .appendQueryParameter("classificationItem", "wbc_106")
                    .appendQueryParameter("_sort", "-classificationYear")
                    .appendQueryParameter("_sort", "-cycle");

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
            InputStreamToCDEClassification inputStreamToCDEClassification = new InputStreamToCDEClassification();
            inputStreamToCDEClassification.readJsonStream(cdePoint, inputStream, CDEPoint.GENERAL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cdePoint;
    }

    @Override
    protected void onPostExecute(CDEPoint cdePoint) {
        mCDEDataFragment.setClassificationText(cdePoint);
    }

}
