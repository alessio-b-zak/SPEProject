package com.epimorphics.android.myrivers.apis;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.epimorphics.android.myrivers.activities.DataViewActivity;
import com.epimorphics.android.myrivers.data.CDEPoint;
import com.epimorphics.android.myrivers.interfaces.OnTaskCompleted;
import com.epimorphics.android.myrivers.models.InputStreamToCDEPoint;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A class handling a geographical(polygon) query to CDE API. Call is made asynchronously in the
 * background.
 *
 * @see CDEPoint
 */
public class CDEPointAPI extends AsyncTask<LatLng, Void, List<CDEPoint>> {
    private static final String TAG = "CDE_API";
    private OnTaskCompleted listener;
    private DataViewActivity mDataViewActivity;

    public CDEPointAPI(OnTaskCompleted listener) {
        this.listener = listener;
        this.mDataViewActivity = (DataViewActivity) listener;
    }

    /**
     * Builds uri, opens an http connection, makes a get request and returns parsed result.
     * All done in the background.
     *
     * @param params LatLng locations of four corners of the screen
     * @return List<CDEPoint> returned from the API call
     */
    @Override
    protected List<CDEPoint> doInBackground(LatLng... params) {
        List<CDEPoint> cdePoints = new ArrayList<CDEPoint>();
        try {
            // Builds an URI
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("ea-cde-pub.epimorphics.net")
                    .appendPath("catchment-planning")
                    .appendPath("so")
                    .appendPath("WaterBody.geojson")
                    .appendQueryParameter("polygon", "{\"type\":\"Polygon\",\"coordinates\":[[["
                            + params[0].longitude + "," + params[0].latitude + "],["
                            + params[1].longitude + "," + params[1].latitude + "],["
                            + params[2].longitude + "," + params[2].latitude + "],["
                            + params[3].longitude + "," + params[3].latitude + "],["
                            + params[0].longitude + "," + params[0].latitude + "]]]}")
                    .appendQueryParameter("type", "River");
            String myUrl = builder.build().toString();
            URL url = new URL(myUrl);

            // Opens a connection and makes a GET request
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();

            Log.d(TAG, "Url is: " + url);
            Log.d(TAG, "The response is: " + response);

            // Parses the response
            InputStream inputStream = conn.getInputStream();
            InputStreamToCDEPoint inputStreamToCDEPoint = new InputStreamToCDEPoint();
            cdePoints = inputStreamToCDEPoint.readJsonStream(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cdePoints;
    }

    /**
     * Called when doInBackground finishes executing. Updates progress spinner visibility and
     * communicates the result to the listener.
     *
     * @param result List<CDEPoint> returned from the API call
     */
    @Override
    protected void onPostExecute(List<CDEPoint> result) {
        mDataViewActivity.getProgressSpinner().setVisibility(View.INVISIBLE);
        listener.onTaskCompletedCDEPoint(result);
    }

}
