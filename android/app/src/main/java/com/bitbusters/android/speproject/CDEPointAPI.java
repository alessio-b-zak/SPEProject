package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import static com.google.android.gms.internal.zznu.is;

public class CDEPointAPI extends AsyncTask<Object, Void, List<CDEPoint>> {
    private static final String TAG = "CDE_API";
    private OnTaskCompleted listener;
    private DataViewActivity mDataViewActivity;

    public CDEPointAPI(OnTaskCompleted listener) {
        this.listener = listener;
        this.mDataViewActivity = (DataViewActivity) listener;
    }

    @Override
    protected List<CDEPoint> doInBackground(Object...params) {
        LatLng[] points = (LatLng[]) params[0];
//        LatLng topLeft = (LatLng) params[0][0];
//        LatLng topRight = (LatLng) params[0][1];
//        LatLng bottomLeft = (LatLng) params[0][2];
//        LatLng bottomRight = (LatLng) params[0][3];
        String dataType = (String) params[1];
        List<CDEPoint> cdePoints = new ArrayList<CDEPoint>();
        // params comes from the execute() call: params[0] is the url.
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("ea-cde-pub.epimorphics.net")
                    .appendPath("catchment-planning")
                    .appendPath("so")
                    .appendPath("WaterBody." + dataType)
                    .appendQueryParameter("polygon", "{\"type\":\"Polygon\",\"coordinates\":[[["
                                          + points[0].longitude + "," + points[0].latitude +"],["
                                          + points[1].longitude + "," + points[1].latitude +"],["
                                          + points[2].longitude + "," + points[2].latitude +"],["
                                          + points[3].longitude + "," + points[3].latitude +"],["
                                          + points[0].longitude + "," + points[0].latitude + "]]]}")
                    .appendQueryParameter("type", "River");
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
            Log.d(TAG, "Url is: " + url);
            Log.d(TAG, "The response is: " + response);
            InputStream inputStream = null;
            inputStream = conn.getInputStream();

            InputStreamToCDEPoint inputStreamToCDEPoint = new InputStreamToCDEPoint();
            cdePoints = inputStreamToCDEPoint.readJsonStream(inputStream);
            //Log.d(DEBUG_TAG, "The result is: " + samplingPoints);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cdePoints;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<CDEPoint> result) {
        mDataViewActivity.getProgressSpinner().setVisibility(View.INVISIBLE);
//        for (CDEPoint r:result){
//            Log.i(TAG, r.getWaterbodyId() + " " + r.getLabel() + " " + r.getLatitude() + " " + r.getLongitude());
//        }
        listener.onTaskCompletedCDEPoint(result);
    }

}
