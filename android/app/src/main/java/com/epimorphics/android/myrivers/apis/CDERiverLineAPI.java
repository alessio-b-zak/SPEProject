package com.epimorphics.android.myrivers.apis;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.epimorphics.android.myrivers.activities.DataViewActivity;
import com.epimorphics.android.myrivers.data.CDEPoint;
import com.epimorphics.android.myrivers.interfaces.OnTaskCompleted;
import com.epimorphics.android.myrivers.models.InputStreamToCDERiverLine;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A class handling a query requesting CDEPoint river line from the CDE API. Call is made
 * asynchronously in the background.
 *
 * @see CDEPoint
 */
public class CDERiverLineAPI extends AsyncTask<CDEPoint, Void, CDEPoint> {
    private static final String TAG = "CDE_API";
    private OnTaskCompleted listener;
    private DataViewActivity mDataViewActivity;

    public CDERiverLineAPI(OnTaskCompleted listener) {
        this.listener = listener;
        this.mDataViewActivity = (DataViewActivity) listener;
    }

    /**
     * Builds uri, opens an http connection, makes a get request and returns parsed result.
     * All done in the background.
     *
     * @param params CDEPoint for which a river line is to be populated
     * @return CDEPoint with a river line populated
     */
    @Override
    protected CDEPoint doInBackground(CDEPoint... params) {
        CDEPoint cdePoint = params[0];
        try {
            // Builds an URI
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("ea-cde-pub.epimorphics.net")
                    .appendPath("catchment-planning")
                    .appendPath("so")
                    .appendPath("WaterBody")
                    .appendPath(cdePoint.getWaterbodyId())
                    .appendPath("river-line");
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
            InputStreamToCDERiverLine inputStreamToCDERiverLine = new InputStreamToCDERiverLine();
            inputStreamToCDERiverLine.readJsonStream(inputStream, cdePoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cdePoint;
    }

    /**
     * Called when doInBackground finishes executing. Updates progress spinner visibility and
     * communicates the result to the listener.
     *
     * @param result CDEPoint with a river line populated
     */
    @Override
    protected void onPostExecute(CDEPoint result) {
        mDataViewActivity.getProgressSpinner().setVisibility(View.INVISIBLE);
        listener.onTaskCompletedCDERiverLine(result);
    }

}
