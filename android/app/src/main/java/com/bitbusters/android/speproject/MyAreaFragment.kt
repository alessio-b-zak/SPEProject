package com.bitbusters.android.speproject

import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.android.gms.maps.model.LatLng
import android.location.Location
import android.view.Gravity
import android.widget.TableLayout
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by mihajlo on 18/07/17.
 */
class MyAreaFragment: FragmentHelper() {

    private val TAG = "MY_AREA_VIEW"

    private lateinit var mDataViewActivity: DataViewActivity
    private lateinit var mBackButton: ImageButton
    private lateinit var mSummaryTable: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataViewActivity = activity as DataViewActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_my_area, container, false)

        MyAreaAPI(this).execute(mDataViewActivity.getCurrentLocation())

        mBackButton = view.findViewById(R.id.back_button_my_area_view) as ImageButton
        mBackButton.setOnClickListener {
            activity.onBackPressed()
        }

        mSummaryTable = view.bind(R.id.my_area_summary_table)

        return view
    }

    fun loadWaterbodyCatchments(myArea: MyArea) {
        MyAreaCatchmentsAPI(this).execute(myArea)
    }

    fun populateData(myArea: MyArea) {
        val parentWeighting = 0.3
        val childWeighting = 0.7

        var tableRow = newTableRow()
        addTextView(tableRow, "Nearest River:", parentWeighting, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, myArea.waterbody, childWeighting, R.style.text_view_table_child, Gravity.START)
        mSummaryTable.addView(tableRow)

        tableRow = newTableRow()
        addTextView(tableRow, "Operational Catchment:", parentWeighting, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, myArea.operationalCatchment, childWeighting, R.style.text_view_table_child, Gravity.START)
        mSummaryTable.addView(tableRow)

        tableRow = newTableRow()
        addTextView(tableRow, "Management Catchment:", parentWeighting, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, myArea.managementCatchment, childWeighting, R.style.text_view_table_child, Gravity.START)
        mSummaryTable.addView(tableRow)

        tableRow = newTableRow()
        addTextView(tableRow, "River Basin District:", parentWeighting, R.style.text_view_table_parent, Gravity.START)
        addTextView(tableRow, myArea.riverBasinDistrict, childWeighting, R.style.text_view_table_child, Gravity.START)
        mSummaryTable.addView(tableRow)
    }

    internal class MyAreaCatchmentsAPI(val mMyAreaFragment: MyAreaFragment):
                   AsyncTask<MyArea, Void, MyArea>() {

        private val TAG = "MY_AREA_CATCHMENTS_API"

        override fun doInBackground(vararg params: MyArea): MyArea {
            val myArea = params[0]
            try {
                val url = URL(myArea.operationalCatchment + ".json")
                val conn = url.openConnection() as HttpURLConnection
                conn.readTimeout = 10000
                conn.connectTimeout = 15000
                conn.requestMethod = "GET"
                conn.doInput = true

                // Starts the query
                conn.connect()
                val response = conn.responseCode
                Log.d(TAG, "Url is: " + url)
                Log.d(TAG, "The response is: " + response)

                val inputStream = conn.inputStream
                val inputStreamToMyAreaCatchments = InputStreamToMyAreaCatchments()
                inputStreamToMyAreaCatchments.readJsonStream(inputStream, myArea)

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return myArea
        }

        // onPostExecute displays the results of the AsyncTask.
        override fun onPostExecute(result: MyArea) {
            mMyAreaFragment.populateData(result)
        }

    }

    internal class MyAreaAPI(val mMyAreaFragment: MyAreaFragment):
                   AsyncTask<Location, Void, MyArea>() {

        private val TAG = "MY_AREA_API"
        private lateinit var myArea: MyArea

        override fun doInBackground(vararg params: Location): MyArea {
            val myLocation = params[0]
            val topLeft: LatLng = LatLng(myLocation.latitude + 0.0005, myLocation.longitude - 0.0005)
            val topRight: LatLng = LatLng(myLocation.latitude + 0.0005, myLocation.longitude + 0.0005)
            val botRight: LatLng = LatLng(myLocation.latitude - 0.0005, myLocation.longitude + 0.0005)
            val botLeft: LatLng = LatLng(myLocation.latitude - 0.0005, myLocation.longitude - 0.0005)
            try {
                val builder = Uri.Builder()
                builder.scheme("http")
                        .authority("ea-cde-pub.epimorphics.net")
                        .appendPath("catchment-planning")
                        .appendPath("so")
                        .appendPath("WaterBody.json")
                        .appendQueryParameter("polygon", "{\"type\":\"Polygon\",\"coordinates\":[[["
                                + topLeft.longitude + "," + topLeft.latitude + "],["
                                + topRight.longitude + "," + topRight.latitude + "],["
                                + botRight.longitude + "," + botRight.latitude + "],["
                                + botLeft.longitude + "," + botLeft.latitude + "],["
                                + topLeft.longitude + "," + topLeft.latitude + "]]]}")
                        .appendQueryParameter("type", "River")
                        .appendQueryParameter("_limit", "1")
                val myUrl = builder.build().toString()

                val url = URL(myUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.readTimeout = 10000
                conn.connectTimeout = 15000
                conn.requestMethod = "GET"
                conn.doInput = true

                // Starts the query
                conn.connect()
                val response = conn.responseCode
                Log.d(TAG, "Url is: " + url)
                Log.d(TAG, "The response is: " + response)

                val inputStream = conn.inputStream
                val inputStreamToMyArea = InputStreamToMyArea()
                myArea = inputStreamToMyArea.readJsonStream(inputStream)

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return myArea
        }

        // onPostExecute displays the results of the AsyncTask.
        override fun onPostExecute(result: MyArea) {
            mMyAreaFragment.loadWaterbodyCatchments(result)
        }

    }

}