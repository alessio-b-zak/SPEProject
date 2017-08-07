package com.epimorphics.android.myrivers.apis

import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.epimorphics.android.myrivers.activities.DataViewActivity
import com.epimorphics.android.myrivers.data.MyArea
import com.epimorphics.android.myrivers.interfaces.OnTaskCompleted
import com.epimorphics.android.myrivers.models.InputStreamToMyAreaCDE
import com.google.android.gms.maps.model.LatLng

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

open class MyAreaCDEAPI(private val listener: OnTaskCompleted) :
        AsyncTask<Any, Void, MyArea>() {

    val mDataViewActivity = listener as DataViewActivity
    private val TAG = "MY_AREA_API"
    lateinit var conn: HttpURLConnection
    private lateinit var myArea: MyArea

    override fun doInBackground(vararg params: Any): MyArea {
        val myLocation = params[0] as Location
        myArea = params[1] as MyArea
        val topLeft: LatLng = LatLng(myLocation.latitude + 0.0005, myLocation.longitude - 0.0005)
        val topRight: LatLng = LatLng(myLocation.latitude + 0.0005, myLocation.longitude + 0.0005)
        val botRight: LatLng = LatLng(myLocation.latitude - 0.0005, myLocation.longitude + 0.0005)
        val botLeft: LatLng = LatLng(myLocation.latitude - 0.0005, myLocation.longitude - 0.0005)

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
        conn = openConnection(url)

        val response = conn.responseCode
        Log.d(TAG, "Url is: " + url)
        Log.d(TAG, "The response is: " + response)

        val inputStream = conn.inputStream
        val inputStreamToMyArea = InputStreamToMyAreaCDE()
        inputStreamToMyArea.readJsonStream(inputStream, myArea)

        return myArea
    }

    // onPostExecute displays the results of the AsyncTask.
    override fun onPostExecute(result: MyArea) {
//        mMyAreaFragment.loadWaterbodyCatchments(result)
        listener.onTaskCompletedMyAreaCDE()
    }

    private fun openConnection(url: URL): HttpURLConnection {
        try {
            conn = url.openConnection() as HttpURLConnection
            conn.readTimeout = 10000
            conn.connectTimeout = 15000
            conn.requestMethod = "GET"
            conn.doInput = true
            conn.instanceFollowRedirects = true
            conn.setRequestProperty("Accept", "application/json")
            HttpURLConnection.setFollowRedirects(true)
            // Starts the query
            conn.connect()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return conn
    }

}
