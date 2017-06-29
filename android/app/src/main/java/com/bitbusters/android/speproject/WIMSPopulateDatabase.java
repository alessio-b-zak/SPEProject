package com.bitbusters.android.speproject;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WIMSPopulateDatabase extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "WIMS_POPULATE_DATABASE";
    private WIMSDbHelper mDbHelper;

    public WIMSPopulateDatabase(Context context) {
        this.mDbHelper = new WIMSDbHelper(context);
    }

    @Override
    protected Void doInBackground(Void...params) {
//        writeSamplingPointsToDatabase();
        Log.i(TAG, "WIMS Point Added to the Database!");
        setLatestMeasurementInDatabase();
        Log.i(TAG, "Latest Measurements Updated!");
        return null;
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Log.i(TAG,"Database Populated!");
        Log.i(TAG,String.valueOf(mDbHelper.numberOfRows()));
    }

    public void writeSamplingPointsToDatabase() {
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("environment.data.gov.uk")
                    .appendPath("water-quality")
                    .appendPath("id")
                    .appendPath("sampling-point")
                    .appendQueryParameter("lat", "54.483784")
                    .appendQueryParameter("long", "-2.114319")
                    .appendQueryParameter("dist", "750")
                    .appendQueryParameter("_limit", "50000")
                    .appendQueryParameter("samplingPointStatus", "open");
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
            InputStream inputStream = null;
            inputStream = conn.getInputStream();

            InputStreamToWIMSPointDatabase inputStreamToWIMSPointDatabase = new InputStreamToWIMSPointDatabase();
            inputStreamToWIMSPointDatabase.readJsonStream(inputStream, mDbHelper);
            //Log.d(DEBUG_TAG, "The result is: " + wimsPoints);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLatestMeasurementInDatabase() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String Query = "Select * from " + WIMSDbHelper.WIMSTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(Query, null);

        cursor.moveToFirst();
        int i = 0;
        while(!cursor.isAfterLast()) {
            String latestMeasurement = cursor.getString(
                    cursor.getColumnIndex(WIMSDbHelper.WIMSTable.COLUMN_NAME_LATEST_MEASURE_DATE));
            if (latestMeasurement == null) {
                try {
                    try {
                        String id = cursor.getString(cursor.getColumnIndex(WIMSDbHelper.WIMSTable.COLUMN_NAME_ID));
                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("http")
                                .authority("environment.data.gov.uk")
                                .appendPath("water-quality")
                                .appendPath("data")
                                .appendPath("measurement")
                                .appendQueryParameter("samplingPoint", id)
                                .appendQueryParameter("_sort", "-sample")
                                .appendQueryParameter("_limit", "1");

                        String myUrl = builder.build().toString();
                        URL url = new URL(myUrl);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000 /* milliseconds */);
                        conn.setConnectTimeout(15000 /* milliseconds */);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);

                        conn.connect();
    //                    int response = conn.getResponseCode();

                        InputStream inputStream = conn.getInputStream();
                        InputStreamToWIMSMeasurementsDatabase inputStreamToWIMSMeasurementsDatabase = new InputStreamToWIMSMeasurementsDatabase();
                        String year = inputStreamToWIMSMeasurementsDatabase.readJsonStream(inputStream);

                        Log.i(TAG, i + ":" + year);

                        mDbHelper.updateRecord(db, WIMSDbHelper.WIMSTable.TABLE_NAME,
                                WIMSDbHelper.WIMSTable.COLUMN_NAME_ID, id,
                                WIMSDbHelper.WIMSTable.COLUMN_NAME_LATEST_MEASURE_DATE, year);
                    } catch (SocketTimeoutException e ){
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cursor.moveToNext();
            i++;
        }
        cursor.close();
        db.close();
    }

}
