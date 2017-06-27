package com.bitbusters.android.speproject;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageDownloader extends AsyncTask<String, Void, Image> {
    private static final String TAG = "IMAGE_DOWNLOADER";
    private ImageView iv;

    public ImageDownloader(ImageView iv) {
        this.iv = iv;
    }

    @Override
    protected Image doInBackground(String...params) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(10, 10, conf);
        List<ImageTag> tags = new ArrayList<>();
        tags.add(ImageTag.NA);
        Image image = new Image("", bmp,0, 0, "", tags, "");
        // param 0 is the id of the image
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .encodedAuthority("139.59.184.70:8080")
                    //.encodedAuthority("172.23.215.243:3000")
                    .appendPath("getImage")
                    .appendPath(params[0]);
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
            InputStreamToImage inputStreamToImage = new InputStreamToImage();
            image = inputStreamToImage.readImageStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Image result) {
        System.out.println(result.getComment());
        iv.setImageBitmap(result.getImage());
    }


}
