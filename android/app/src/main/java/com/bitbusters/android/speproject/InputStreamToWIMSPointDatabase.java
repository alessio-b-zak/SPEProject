package com.bitbusters.android.speproject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.bitbusters.android.speproject.WIMSDbHelper.WIMSTable.COLUMN_NAME_ID;
import static com.bitbusters.android.speproject.WIMSDbHelper.WIMSTable.COLUMN_NAME_LATITUDE;
import static com.bitbusters.android.speproject.WIMSDbHelper.WIMSTable.COLUMN_NAME_LONGITUDE;
import static com.bitbusters.android.speproject.WIMSDbHelper.WIMSTable.TABLE_NAME;

/**
 * Created by cp153 on 06/12/2016.
 */

public class InputStreamToWIMSPointDatabase {

    private static final String TAG = "WIMS_POPULATE_DATABASE";

    public void readJsonStream(InputStream in, WIMSDbHelper mDbHelper) throws IOException {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readMessagesArray(reader, mDbHelper, db);
        } finally {
            reader.close();
        }
    }

    public void readMessagesArray(JsonReader reader, WIMSDbHelper mDbHelper, SQLiteDatabase db) throws IOException {
        try {
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("items")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        readMessage(reader, mDbHelper, db);
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readMessage(JsonReader reader, WIMSDbHelper mDbHelper, SQLiteDatabase db) throws IOException {
        String id = null;
        String samplingPointType = null;
        String label = null;
        double latitude = 0.0, longitude = 0.0;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("notation")) {
                    id = reader.nextString();
                } else if (name.equals("lat")) {
                    latitude = reader.nextDouble();
                } else if (name.equals("long")) {
                    longitude = reader.nextDouble();
                } else if (name.equals("label")) {
                    label = reader.nextString();
                } else if (name.equals("samplingPointType")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("label")) {
                            samplingPointType = reader.nextString();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ID, id);
        values.put(COLUMN_NAME_LATITUDE, latitude);
        values.put(COLUMN_NAME_LONGITUDE, longitude);

        if (!mDbHelper.isRecordInTable(TABLE_NAME, COLUMN_NAME_ID, id)) {
            db.insert(TABLE_NAME, null, values);
        }

    }
}
