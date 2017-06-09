
package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageUploader extends AsyncTask<Image, Void, String> {
    private static final String TAG = "IMAGES_UPLOADER";

    @Override
    protected String doInBackground(Image...params) {
        try {
            String comment = params[0].getComment();
            Bitmap bitmap = params[0].getImage();
            Double latitude = params[0].getLatitude();
            Double longitude = params[0].getLongitude();
            PhotoTag tag = params[0].getPhotoTag();

            HttpURLConnection httpUrlConnection = null;
            URL url = new URL("http://139.59.184.70:8080/uploadImage");
            Log.i(TAG, " URL UPLOAD : " + url.toString());
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            Log.i(TAG, "Set request body to true");
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Comment", comment);
            httpUrlConnection.setRequestProperty("Tag", tag.name());
            httpUrlConnection.setRequestProperty("Latitude", String.valueOf(latitude));
            httpUrlConnection.setRequestProperty("Longitude", String.valueOf(longitude));
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "image/jpeg");

            Log.i(TAG, "Url is: " + url);

            OutputStream request = httpUrlConnection.getOutputStream();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, request);

            byte[] byteArray = stream.toByteArray();
            request.write(byteArray);
            Log.i(TAG, "Image Converted to JPEG ");
            stream.close();
            request.close();

            // Get response:
            InputStream responseStream =
                    new BufferedInputStream(httpUrlConnection.getInputStream());
            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();
            responseStream.close();

            // Close the connection:
            httpUrlConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Upload Successful";

    }
    // onPostExecute displays the results of the AsyncTask.
    protected void onPostExecute(int result) {
        System.out.println("ImageUploader onPostExecute called.");
    }


}
