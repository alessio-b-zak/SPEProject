package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CDEPointDetailRatingsAPI extends AsyncTask<CDEPoint, Void, CDEPoint> {

    private static final String TAG = "CDE_DETAIL_RATINGS";
    private CDEDetailsFragment mCDEDetailsFragment;

    public CDEPointDetailRatingsAPI(CDEDetailsFragment context) {
        this.mCDEDetailsFragment = context;
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
                    .appendQueryParameter("classificationItem", "wbc_13")
                    .appendQueryParameter("classificationItem", "wbc_5")
                    .appendQueryParameter("classificationItem", "wbc_228")
                    .appendQueryParameter("classificationItem", "wbc_6")
                    .appendQueryParameter("classificationItem", "wbc_7")
                    .appendQueryParameter("classificationItem", "wbc_8")
                    .appendQueryParameter("classificationItem", "wbc_10")
                    .appendQueryParameter("classificationItem", "wbc_229")
                    .appendQueryParameter("classificationItem", "wbc_9")
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
            inputStreamToCDEClassification.readJsonStream(cdePoint, inputStream, CDEPoint.DETAIL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cdePoint;
    }

    @Override
    protected void onPostExecute(CDEPoint cdePoint) {
        mCDEDetailsFragment.setSubClassificationText(cdePoint);
    }

}
