package com.bitbusters.android.speproject;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;

/**
 * Created by mihajlo on 28/06/17.
 */

public class WIMSDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "WIMS_DATABASE_HELPER";
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WIMS.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + WIMSTable.TABLE_NAME + " (" +
                    WIMSTable._ID + " INTEGER PRIMARY KEY," +
                    WIMSTable.COLUMN_NAME_ID + " TEXT," +
                    WIMSTable.COLUMN_NAME_LATITUDE + " FLOAT," +
                    WIMSTable.COLUMN_NAME_LONGITUDE + " FLOAT," +
                    WIMSTable.COLUMN_NAME_LATEST_MEASURE_DATE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WIMSTable.TABLE_NAME;

    public WIMSDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, WIMSTable.TABLE_NAME);
    }

    public boolean isRecordInTable(String TableName, String dbfield, String fieldValue) {
        SQLiteDatabase db = this.getReadableDatabase();
//        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
//        Cursor cursor = db.rawQuery(Query, null);

        String[] projection = { dbfield };

        String selection = dbfield + " = ?";
        String[] selectionArgs = { fieldValue };

        Cursor cursor = db.query(
                TableName,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int numberOfNulls() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = { WIMSTable.COLUMN_NAME_LATEST_MEASURE_DATE };

        String selection = WIMSTable.COLUMN_NAME_LATEST_MEASURE_DATE + " is null";

        String[] selectionArgs = { };

        Cursor cursor = db.query(
                WIMSTable.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    public List<WIMSPoint> getWIMSPointsWithin(double latitude1, double longitude1, double latitude3,
                                               double longitude3, Integer year) {

        List<WIMSPoint> wimsPointList = new ArrayList<>();
        WIMSPoint wimsPoint;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = { WIMSTable.COLUMN_NAME_ID,
                                WIMSTable.COLUMN_NAME_LATITUDE,
                                WIMSTable.COLUMN_NAME_LONGITUDE,
                                WIMSTable.COLUMN_NAME_LATEST_MEASURE_DATE };

        String selection = WIMSTable.COLUMN_NAME_LATITUDE + " >= ? AND " +
                           WIMSTable.COLUMN_NAME_LATITUDE + " <= ? AND " +
                           WIMSTable.COLUMN_NAME_LONGITUDE + " >= ? AND " +
                           WIMSTable.COLUMN_NAME_LONGITUDE + " <= ? AND " +
                           WIMSTable.COLUMN_NAME_LATEST_MEASURE_DATE + " = ?";

        String[] selectionArgs = { String.valueOf(latitude3),
                                   String.valueOf(latitude1),
                                   String.valueOf(longitude1),
                                   String.valueOf(longitude3),
                                   String.valueOf(year)};

        Cursor cursor = db.query(
                WIMSTable.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            wimsPointList.add(new WIMSPoint(cursor.getString(cursor.getColumnIndex(WIMSTable.COLUMN_NAME_ID)),
                                            cursor.getDouble(cursor.getColumnIndex(WIMSTable.COLUMN_NAME_LATITUDE)),
                                            cursor.getDouble(cursor.getColumnIndex(WIMSTable.COLUMN_NAME_LONGITUDE))));
//            Log.i(TAG,cursor.getString(cursor.getColumnIndex(WIMSTable.COLUMN_NAME_LATEST_MEASURE_DATE)));
            cursor.moveToNext();
        }
        cursor.close();
        return wimsPointList;
    }

    public void updateRecord(SQLiteDatabase db, String tableName, String searchField,
                             String searchValue, String updateField, String updateValue) {


        ContentValues values = new ContentValues();
        values.put(updateField, updateValue);

        // Which row to update, based on the title
        String selection = searchField + " LIKE ?";
        String[] selectionArgs = { searchValue };

        int count = db.update(
                tableName,
                values,
                selection,
                selectionArgs);

    }

    public void exportDatabase(String packageName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                Log.i(TAG, "Exporting Database");
                String currentDBPath = "//data//"+  packageName +"//databases//"+DATABASE_NAME+"";
                String backupDBPath = DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }

                Log.i(TAG, "Database Exported to : " + backupDB.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class WIMSTable implements BaseColumns{

        private WIMSTable() {}

        public static final String TABLE_NAME = "wims";
        public static final String COLUMN_NAME_ID = "sample_point_id";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LATEST_MEASURE_DATE = "latest_measure_date";
    }
}
