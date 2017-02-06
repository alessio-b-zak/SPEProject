package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
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

public class ImageUploader extends AsyncTask<String, Void, List<Image>> {
    private static final String DEBUG_TAG = "SAMPLING_POINTS_API";
//    private OnTaskCompleted listener;

//    public ImagesDownloader(OnTaskCompleted listener) {
//        this.listener = listener;
//    }

    @Override
    protected List<Image> doInBackground(String...params) {
        List<Image> images = new ArrayList<Image>();
        try {
//            int[] imgTmp = new int[]{0, 255, 255, 0};
//            Bitmap bitmap = Bitmap.createBitmap(imgTmp, 2, 2, Bitmap.Config.ARGB_8888);
            Bitmap bitmap = BitmapFactory.decodeFile("C:\\Users\\cp153\\Desktop\\SPE\\SPE\\server\\routes\\uploads\\pollution.bmp");
//            Bitmap bitmap = BitmapFactory.decodeByteArray(imgTmp, 0, imgTmp.length);
            if (bitmap == null) {
                System.out.println("Bitmap image is null. Aborting...");
            }
//            Bitmap bitmap = myView.getBitmap(); // Copy bitmap image here.

            String attachmentName = "bitmap";
            String attachmentFileName = "bitmap.bmp";
            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";

            HttpURLConnection httpUrlConnection = null;
            URL url = new URL("http://10.101.135.248:3000/uploadImage");
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);

            // Start content wrapper:

            DataOutputStream request = new DataOutputStream(
                    httpUrlConnection.getOutputStream());

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);
            // Convert Bitmap to ByteBuffer:

            // Each pixel is a byte.
            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            for (int i = 0; i < bitmap.getWidth(); ++i) {
                for (int j = 0; j < bitmap.getHeight(); ++j) {
                    pixels[i * bitmap.getHeight() + j] = (byte) bitmap.getPixel(i, j);
                }
            }

            request.write(pixels);

            // End content wrapper:

            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

            request.flush();
            request.close();
            // Get response:

            InputStream responseStream = new
                    BufferedInputStream(httpUrlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String response = stringBuilder.toString();
            // Close response stream:
            Log.d(DEBUG_TAG, "Url is: " + url);
            Log.d(DEBUG_TAG, "The response is: " + response);
            responseStream.close();
            // Close the connection:
            httpUrlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<Image> result) {
//        listener.onTaskCompleted(result);
        System.out.println("ImageUploader onPostExecute called.");
//        for (Image img:result) {
//            System.out.println(img.getComment());
//        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
