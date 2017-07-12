package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.bitbusters.android.speproject.CDEPoint.OBJECTIVE;
import static com.bitbusters.android.speproject.CDEPoint.PREDICTED;

public class CDEPointRatingsAPI extends AsyncTask<Object, Void, CDEPoint> {

    private static final String TAG = "CDE_GENERAL_RATINGS";
    private OnTaskCompleted listener;

    public CDEPointRatingsAPI(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected CDEPoint doInBackground(Object...params) {
        CDEPoint cdePoint = (CDEPoint) params[0];
        String group = (String) params[1];
        String linkExtension = setLinkExtension(group);
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("ea-cde-pub.epimorphics.net")
                    .appendPath("catchment-planning")
                    .appendPath("data")
                    .appendPath("classification" + linkExtension + ".json")
                    .appendQueryParameter("waterBody", cdePoint.getWaterbodyId())
                    .appendQueryParameter("classificationItem", "wbc_1")
                    .appendQueryParameter("classificationItem", "wbc_2")
                    .appendQueryParameter("classificationItem", "wbc_106")
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
            inputStreamToCDEClassification.readJsonStream(cdePoint, inputStream, group);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cdePoint;
    }

    @Override
    protected void onPostExecute(CDEPoint cdePoint) {
        listener.onTaskCompletedCDEPointRatings(cdePoint);
    }

    private String setLinkExtension(String group) {
        String linkExtension = "";
        switch (group) {
            case OBJECTIVE:
                linkExtension = "-objective-outcome";
                break;
            case PREDICTED:
                linkExtension = "-predicted-outcome";
                break;
            default:
                break;
        }
        return linkExtension;
    }
}
