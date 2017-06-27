package com.bitbusters.android.speproject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ThumbnailsDownloader extends AsyncTask<String, Void, List<Image>> {
    private static final String TAG = "THUMBNAIL_DOWNLOADER";
    private DataViewActivity mDataViewActivity;
    private ThumbnailsDownloadListener mThumbnailsDownloadListener;

    public ThumbnailsDownloader(Context context, ThumbnailsDownloadListener imgLocationDownloadListener) {
        this.mDataViewActivity = (DataViewActivity) context;
        this.mThumbnailsDownloadListener = imgLocationDownloadListener;
    }

    @Override
    protected List<Image> doInBackground(String...params) {

        List<Image> images = new ArrayList<>();
        // params comes from the execute() call: params[0,1,2,3] are lat and long of points 1 and 3.
        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("139.59.184.70:8080")
                    //.encodedAuthority("172.23.215.243:3000")
                    .appendPath("getThumbnails")
                    .appendPath(params[0])
                    .appendPath(params[1])
                    .appendPath(params[2])
                    .appendPath(params[3]);
            String myUrl = builder.build().toString();
            Log.i(TAG, myUrl);
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
            InputStream inputStream = null;
            inputStream = conn.getInputStream();
            //len limits the input string returned. should be changed from 5000 when tested.
            int len = 5000;
            InputStreamToThumbnail inputStreamToThumbnail = new InputStreamToThumbnail();
            images = inputStreamToThumbnail.readImageStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<Image> result) {
        mDataViewActivity.getPhotoMarkers().clear();
        Log.i(TAG,"Number of images returned: " + String.valueOf(result.size())); // RETURNING 0.
        for (Image img : result) {
            GalleryItem photo = new GalleryItem(img.getLongitude(), img.getLatitude(),img.getId(), img.getTags(), img.getComment(),img.getId(), img.getImage(), img.getDate());
            mDataViewActivity.getPhotoMarkers().add(photo);
            mDataViewActivity.getPictureClusterManager().addItem(photo);
            //set image in gallery
        }
        mDataViewActivity.getPictureClusterManager().cluster();
        mDataViewActivity.getProgressSpinner().setVisibility(View.INVISIBLE);
        mThumbnailsDownloadListener.imagesDownloaded();

    }

}
