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
    private WIMSDbHelper mDbHelper;
    private OnTaskCompleted listener;
    private DataViewActivity mDataViewActivity;

    public WIMSPointAPIDatabase(OnTaskCompleted listener, WIMSDbHelper mDbHelper) {
        this.listener = listener;
        this.mDataViewActivity = (DataViewActivity) listener;
        this.mDbHelper = mDbHelper;
    }

    @Override
    protected List<WIMSPoint> doInBackground(String...params) {
        return mDbHelper.getWIMSPointsWithin(Double.valueOf(params[0]), Double.valueOf(params[1]),
                                                   Double.valueOf(params[2]), Double.valueOf(params[3]));
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List<WIMSPoint> result) {
        mDataViewActivity.getProgressSpinner().setVisibility(View.INVISIBLE);
        listener.onTaskCompletedWIMSPoint(result);
    }

}
