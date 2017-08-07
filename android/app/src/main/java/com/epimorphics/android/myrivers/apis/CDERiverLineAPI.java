package com.epimorphics.android.myrivers.apis;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.epimorphics.android.myrivers.activities.DataViewActivity;
import com.epimorphics.android.myrivers.data.CDEPoint;
import com.epimorphics.android.myrivers.interfaces.OnTaskCompleted;
import com.epimorphics.android.myrivers.models.InputStreamToCDERiverLine;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//import static com.google.android.gms.internal.zznu.is;

public class CDERiverLineAPI extends AsyncTask<CDEPoint, Void, CDEPoint> {
    private static final String TAG = "CDE_API";
    private OnTaskCompleted listener;
    private DataViewActivity mDataViewActivity;

    public CDERiverLineAPI(OnTaskCompleted listener) {
        this.listener = listener;
        this.mDataViewActivity = (DataViewActivity) listener;
    }

    @Override
    protected CDEPoint doInBackground(CDEPoint... params) {
        CDEPoint cdePoint = params[0];
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("ea-cde-pub.epimorphics.net")
                    .appendPath("catchment-planning")
                    .appendPath("so")
                    .appendPath("WaterBody")
                    .appendPath(cdePoint.getWaterbodyId())
                    .appendPath("river-line");
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
            Log.d(TAG, "Url is: " + url);
            Log.d(TAG, "The response is: " + response);

            InputStream inputStream = conn.getInputStream();

            InputStreamToCDERiverLine inputStreamToCDERiverLine = new InputStreamToCDERiverLine();
            inputStreamToCDERiverLine.readJsonStream(inputStream, cdePoint);
            //Log.d(DEBUG_TAG, "The result is: " + samplingPoints);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cdePoint;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(CDEPoint result) {
        mDataViewActivity.getProgressSpinner().setVisibility(View.INVISIBLE);
        listener.onTaskCompletedCDERiverLine(result);
    }

}
