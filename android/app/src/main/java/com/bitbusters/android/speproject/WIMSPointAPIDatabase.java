package com.bitbusters.android.speproject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import static com.google.android.gms.internal.zznu.is;

public class WIMSPointAPIDatabase extends AsyncTask<String, Void, List<WIMSPoint>> {
    private static final String DEBUG_TAG = "SAMPLING_POINTS_API";
    private OnTaskCompleted listener;
    private DataViewActivity mDataViewActivity;

    public WIMSPointAPIDatabase(OnTaskCompleted listener) {
        this.listener = listener;
        this.mDataViewActivity = (DataViewActivity) listener;
    }

    @Override
    protected List<WIMSPoint> doInBackground(String...params) {
        List<WIMSPoint> wimsPoints = new ArrayList<WIMSPoint>();



        return wimsPoints;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<WIMSPoint> result) {
        mDataViewActivity.getProgressSpinner().setVisibility(View.INVISIBLE);
        //TODO: Make sure that results are passed back to the caller;
//        for (WIMSPoint r:result){
//            System.out.println(r.getId() + " " + r.getLatitude() + " " + r.getLongitude() + " " + r.getSamplingPointType() + " ");
//        }

        listener.onTaskCompletedWIMSPoint(result);
    }

    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
