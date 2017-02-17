package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
//import static com.google.android.gms.internal.zznu.is;

public class ImagesLocationDownloader extends AsyncTask<String, Void, List<ImageLocation>> {
    private static final String DEBUG_TAG = "IMAGES_LOC";
//    private OnTaskCompleted listener;

//    public ImagesLocationDownloader(OnTaskCompleted listener) {
//        this.listener = listener;
//    }

    @Override
    protected List<ImageLocation> doInBackground(String...params) {
        List<ImageLocation> images = new ArrayList<ImageLocation>();
        // params comes from the execute() call: params[0,1,2,3] are lat and long of points 1 and 3.
        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("192.168.0.29:3000")
                    .appendPath("getImagesLocation")
                    .appendPath(params[0])
                    .appendPath(params[1])
                    .appendPath(params[2])
                    .appendPath(params[3]);
            String myUrl = builder.build().toString();
            Log.d(DEBUG_TAG, myUrl);
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
            InputStreamToImageLocation inputStreamToImageLoc = new InputStreamToImageLocation();
            images = inputStreamToImageLoc.readImageLocStream(inputStream);
//            Log.d(DEBUG_TAG, "The result is: " + SamplingPoints);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<ImageLocation> result) {
//        listener.onTaskCompleted(result);
        for (ImageLocation img:result) {
            System.out.println(img.getId());
            System.out.println(img.getLatitude());
            System.out.println(img.getLongitude());
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}