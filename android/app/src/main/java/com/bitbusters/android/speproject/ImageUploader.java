
package com.bitbusters.android.speproject;

import android.os.AsyncTask;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
            List<ImageTag> tags = params[0].getTags();
            String sendTags = params[0].getTagsString();

            Log.e(TAG, "Tags Sent: " + sendTags);

            HttpURLConnection httpUrlConnection = null;
            URL url = new URL("http://139.59.184.70:8080/uploadImage");
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            Log.i(TAG, "Set request body to true");
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Comment", comment);
            httpUrlConnection.setRequestProperty("Tags", sendTags);
            httpUrlConnection.setRequestProperty("Latitude", String.valueOf(latitude));
            httpUrlConnection.setRequestProperty("Longitude", String.valueOf(longitude));
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "image/jpeg");

            Log.i(TAG, "Url is: " + url);

            OutputStream request = httpUrlConnection.getOutputStream();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, request);

            byte[] byteArray = stream.toByteArray();
            request.write(byteArray);
            Log.i(TAG, "Image Compressed");
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
