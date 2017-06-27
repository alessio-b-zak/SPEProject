package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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
        // params comes from the execute() call: params[0] is the url.
        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("environment.data.gov.uk")
                    .appendPath("water-quality")
                    .appendPath("id")
                    .appendPath("sampling-point")
                    .appendQueryParameter("lat", params[0])
                    .appendQueryParameter("long", params[1])
                    .appendQueryParameter("dist", params[2])
                    .appendQueryParameter("samplingPointStatus", "open");
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
            InputStream inputStream = null;
            inputStream = conn.getInputStream();

            //len limits the input string returned. should be changed from 5000 when tested.
            int len = 5000;
            // Convert the InputStream into a string
//            String SamplingPoints = readIt(is, len);
            InputStreamToWIMSPoint inputStreamToWIMSPoint = new InputStreamToWIMSPoint();
            wimsPoints = inputStreamToWIMSPoint.readJsonStream(inputStream);
            //Log.d(DEBUG_TAG, "The result is: " + wimsPoints);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return wimsPoints;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<WIMSPoint> result) {
        mDataViewActivity.getProgressSpinner().setVisibility(View.INVISIBLE);
        //TODO: Make sure that results are passed back to the caller;
//        for (WIMSPoint r:result){
//            System.out.println(r.getId() + " " + r.getLatitude() + " " + r.getLongitude() + " " + r.getSamplingPointType() + " ");
//        }

        listener.onTaskCompletedWIMSPoint(result);
    }

    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}