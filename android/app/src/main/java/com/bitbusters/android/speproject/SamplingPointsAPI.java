package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.bitbusters.android.speproject.R.id.textView;
//import static com.google.android.gms.internal.zznu.is;

public class SamplingPointsAPI extends AsyncTask<String, Void, String> {
    private static final String DEBUG_TAG = "SAMPLING_POINTS_API";
    @Override
    protected String doInBackground(String...params) {

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
                    .appendQueryParameter("dist", "10")
                    .appendQueryParameter("samplingPointStatus", "open");
            String myUrl = builder.build().toString();

            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            // TODO: Find out why it works only when true!
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "Url is: " + url);
            Log.d(DEBUG_TAG, "The response is: " + response);
            InputStream is = null;
            is = conn.getInputStream();
            //len limits the input string returned. should be changed from 5000 when tested.
            int len = 5000;
            // Convert the InputStream into a string
            String SamplingPoints = readIt(is, len);
//            Log.d(DEBUG_TAG, "The result is: " + SamplingPoints);
            return SamplingPoints;
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        //TODO: Make sure that results are passed back to the caller;
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
