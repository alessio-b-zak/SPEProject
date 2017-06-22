package com.bitbusters.android.speproject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImagesLocationDownloader extends AsyncTask<String, Void, List<ImageLocation>> {
    private static final String TAG = "IMAGES_LOC";
    private DataViewActivity tempDVA;
    private ImageLocationDownloadListener imglis;

    public ImagesLocationDownloader(Context context, ImageLocationDownloadListener imglis) {
        this.tempDVA = (DataViewActivity) context;
        this.imglis = imglis;
    }

    @Override
    protected List<ImageLocation> doInBackground(String...params) {

        List<ImageLocation> images = new ArrayList<>();
        // params comes from the execute() call: params[0,1,2,3] are lat and long of points 1 and 3.
        // param[0] = lat for top left corner.
        // param[1] = long for top left corner.
        // param[2] = lat for bottom right corner.
        // param[3] = long for bottom right corner.
        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("139.59.184.70:8080")
                    //.encodedAuthority("172.23.215.243:3000")
                    .appendPath("getImagesLocation")
                    .appendPath(params[0])
                    .appendPath(params[1])
                    .appendPath(params[2])
                    .appendPath(params[3]);
            String myUrl = builder.build().toString();
            Log.d(TAG, myUrl);
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
            //len limits the input string returned. should be changed from 5000 when tested.
            int len = 5000;
            // Convert the InputStream into a string
//            String SamplingPoints = readIt(is, len);
            InputStreamToImageLocation inputStreamToImageLoc = new InputStreamToImageLocation();
            images = inputStreamToImageLoc.readImageLocStream(inputStream);
//            Log.d(TAG, "The result is: " + SamplingPoints);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<ImageLocation> result) {
//        listener.onTaskCompletedSamplingPoint(result);

        tempDVA.getPhotoMarkers().clear();

        Log.e("here!!", "1");
        for (ImageLocation img : result) {
            Log.e("results!!", "1");
            System.out.println(img.getId());
            System.out.println(img.getPhotoTag().name());
            System.out.println(img.getLatitude());
            System.out.println(img.getLongitude());

            //PicturePoint photo = new PicturePoint(img.getLongitude(), img.getLatitude(), img.getId());
            //tempDVA.getPhotoMarkers().add(photo);
            //tempDVA.getPictureClusterManager().addItem(photo);

        }
        Log.e(String.valueOf(tempDVA.getPhotoMarkers().size()),"1");
        tempDVA.getPictureClusterManager().cluster();
        imglis.imagesDownloaded();


    }

}
