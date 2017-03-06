package com.bitbusters.android.speproject;

import android.content.Context;
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

public class ThumbnailsDownloader extends AsyncTask<String, Void, List<Image>> {
    private static final String DEBUG_TAG = "THUMBNAIL_DOWNLOADER";
    private DataViewActivity mDataViewActivity;
    private ImgLocDowListener mImglis;
//    private OnTaskCompleted listener;

//    public ImagesDownloader(OnTaskCompleted listener) {
//        this.listener = listener;
//    }

    public ThumbnailsDownloader(Context context, ImgLocDowListener imglis) {
        this.mDataViewActivity = (DataViewActivity) context;
        this.mImglis = imglis;
    }

    @Override
    protected List<Image> doInBackground(String...params) {
        List<Image> images = new ArrayList<>();
        // params comes from the execute() call: params[0,1,2,3] are lat and long of points 1 and 3.
        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("139.59.184.70:3000")
                    .appendPath("getThumbnails")
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
            InputStreamToThumbnail inputStreamToThumbnail = new InputStreamToThumbnail();
            images = inputStreamToThumbnail.readImageStream(inputStream);
//            Log.d(DEBUG_TAG, "The result is: " + SamplingPoints);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<Image> result) {
        mDataViewActivity.getPhotoMarkers().clear();
        Log.e(String.valueOf(result.size()),"this2?"); // RETURNING 0.
        for (Image img : result) {
            GalleryItem photo = new GalleryItem(img.getLongitude(), img.getLatitude(),img.getId(), img.getPhotoTag().toString(), img.getComment(),img.getId(), img.getImage());
            mDataViewActivity.getPhotoMarkers().add(photo);
            mDataViewActivity.getPictureClusterManager().addItem(photo);
            //set image in gallery
        }
        Log.e(String.valueOf(mDataViewActivity.getPhotoMarkers().size()),"this?"); // RETURNING 0.
        mDataViewActivity.getPictureClusterManager().cluster();
        mImglis.imagesDownloaded();


    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
