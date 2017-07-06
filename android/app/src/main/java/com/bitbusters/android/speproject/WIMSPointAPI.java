package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import static com.google.android.gms.internal.zznu.is;

public class WIMSPointAPI extends AsyncTask<String, Void, List<WIMSPoint>> {
    private static final String DEBUG_TAG = "SAMPLING_POINTS_API";
    private OnTaskCompleted listener;
    private DataViewActivity mDataViewActivity;

    public WIMSPointAPI(OnTaskCompleted listener) {
        this.listener = listener;
        this.mDataViewActivity = (DataViewActivity) listener;
    }

    @Override
    protected List<WIMSPoint> doInBackground(String...params) {
        List<WIMSPoint> wimsPoints = new ArrayList<WIMSPoint>();
        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("139.59.184.70:8080")
                    //.encodedAuthority("172.23.215.243:3000")
                    .appendPath("getThumbnails")
                    .appendPath(params[0])
                    .appendPath(params[1])
                    .appendPath(params[2])
                    .appendPath(params[3])
                    .appendPath(params[4]);
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
            Log.d(DEBUG_TAG, "Url is: " + url);
            Log.d(DEBUG_TAG, "The response is: " + response);
            InputStream inputStream = conn.getInputStream();

            InputStreamToWIMSPoint inputStreamToWIMSPoint = new InputStreamToWIMSPoint();
            wimsPoints = inputStreamToWIMSPoint.readJsonStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return wimsPoints;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<WIMSPoint> result) {
        mDataViewActivity.getProgressSpinner().setVisibility(View.INVISIBLE);
        listener.onTaskCompletedWIMSPoint(result);
    }

}
