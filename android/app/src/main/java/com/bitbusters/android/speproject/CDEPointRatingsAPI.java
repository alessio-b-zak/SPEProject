package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.bitbusters.android.speproject.CDEPoint.OVERALL;

public class CDEPointRatingsAPI extends AsyncTask<Object, Void, List<Object>> {

    private static final String TAG = "CDE_POINT_RATINGS";
    private CDEDataFragment mCDEDataFragment;

    public CDEPointRatingsAPI(CDEDataFragment context) {
        this.mCDEDataFragment = context;
    }

    @Override
    protected List<Object> doInBackground(Object...params) {
        CDEPoint cdePoint = (CDEPoint) params[0];
        String group = (String) params[1];
        try {
            String myUrl = buildUrl(group, cdePoint.getWaterbodyId());
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
            cdePoint.printClassification();

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Object> result = new ArrayList<Object>();
        result.add(cdePoint);
        result.add(group);

        return result;
    }

    @Override
    protected void onPostExecute(List<Object> result) {
        CDEPoint cdePoint = (CDEPoint) result.get(0);
        String group = (String) result.get(1);
        if (group.equals(OVERALL)){
            mCDEDataFragment.setClassificationText(cdePoint);
        } else {
            mCDEDataFragment.setSubClassificationText(cdePoint);
        }

    }

    private String buildUrl(String group, String waterbodyId) {
        Uri.Builder builder = new Uri.Builder();
        switch (group) {
            case (CDEPoint.CHEMICAL):
                builder.scheme("http")
                        .authority("ea-cde-pub.epimorphics.net")
                        .appendPath("catchment-planning")
                        .appendPath("data")
                        .appendPath("classification.json")
                        .appendQueryParameter("waterBody", waterbodyId)
                        .appendQueryParameter("classificationItem", "wbc_13")
                        .appendQueryParameter("classificationItem", "wbc_5")
                        .appendQueryParameter("classificationItem", "wbc_228")
                        .appendQueryParameter("classificationItem", "wbc_6")
                        .appendQueryParameter("classificationItem", "wbc_7")
                        .appendQueryParameter("classificationItem", "wbc_8")
                        .appendQueryParameter("_sort", "-classificationYear")
                        .appendQueryParameter("_sort", "-cycle");
                break;
            case (CDEPoint.ECOLOGICAL):
                builder.scheme("http")
                        .authority("ea-cde-pub.epimorphics.net")
                        .appendPath("catchment-planning")
                        .appendPath("data")
                        .appendPath("classification.json")
                        .appendQueryParameter("waterBody", waterbodyId)
                        .appendQueryParameter("classificationItem", "wbc_10")
                        .appendQueryParameter("classificationItem", "wbc_229")
                        .appendQueryParameter("classificationItem", "wbc_9")
                        .appendQueryParameter("_sort", "-classificationYear")
                        .appendQueryParameter("_sort", "-cycle");
                break;
        }

        return builder.build().toString();
    }

}
