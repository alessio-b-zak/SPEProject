package com.bitbusters.android.speproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

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

    public List<WIMSPoint> getWIMSPointsWithin(double latitude1, double longitude1,
                                              double latitude3, double longitude3) {

        List<WIMSPoint> wimsPointList = new ArrayList<>();
        WIMSPoint wimsPoint;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = { WIMSTable.COLUMN_NAME_ID,
                                WIMSTable.COLUMN_NAME_LATITUDE,
                                WIMSTable.COLUMN_NAME_LONGITUDE};

        String selection = WIMSTable.COLUMN_NAME_LATITUDE + " >= ? AND " +
                           WIMSTable.COLUMN_NAME_LATITUDE + " <= ? AND " +
                           WIMSTable.COLUMN_NAME_LONGITUDE + " >= ? AND " +
                           WIMSTable.COLUMN_NAME_LONGITUDE + " <= ?";

        String[] selectionArgs = { String.valueOf(latitude3),
                                   String.valueOf(latitude1),
                                   String.valueOf(longitude1),
                                   String.valueOf(longitude3) };

        Cursor cursor = db.query(
                WIMSTable.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            wimsPointList.add(new WIMSPoint(cursor.getString(cursor.getColumnIndex(WIMSTable.COLUMN_NAME_ID)),
                                            cursor.getDouble(cursor.getColumnIndex(WIMSTable.COLUMN_NAME_LATITUDE)),
                                            cursor.getDouble(cursor.getColumnIndex(WIMSTable.COLUMN_NAME_LONGITUDE))));
            Log.i(TAG,cursor.getString(cursor.getColumnIndex(WIMSTable.COLUMN_NAME_LATEST_MEASURE_DATE)));
            cursor.moveToNext();
        }
        cursor.close();
        return wimsPointList;
    }

    public void updateRecord(String tableName, String searchField, String searchValue,
                             String updateField, String updateValue) {

        SQLiteDatabase db = this.getReadableDatabase();

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

    public static class WIMSTable implements BaseColumns{

        private WIMSTable() {}

        public static final String TABLE_NAME = "wims";
        public static final String COLUMN_NAME_ID = "sample_point_id";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LATEST_MEASURE_DATE = "latest_measure_date";
    }
}
