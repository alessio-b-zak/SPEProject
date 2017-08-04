package com.bitbusters.android.speproject.apis

import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.bitbusters.android.speproject.activities.DataViewActivity
import com.bitbusters.android.speproject.data.WIMSPoint
import com.bitbusters.android.speproject.interfaces.OnTaskCompleted
import com.bitbusters.android.speproject.models.InputStreamToWIMSPoint
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

open class WIMSPointAPI(private val listener: OnTaskCompleted) :
        AsyncTask<String, Void, List<WIMSPoint>>() {

    val mDataViewActivity = listener as DataViewActivity
    lateinit var conn: HttpURLConnection

    private val TAG = "WIMS_POINTS_API"

    override fun doInBackground(vararg params: String): List<WIMSPoint> {
        val builder = Uri.Builder()
        builder.scheme("http")
                .encodedAuthority("139.59.184.70:8080")
                //.encodedAuthority("172.23.215.243:3000")
                .appendPath("getWIMSPoints")
                .appendPath(params[0])
                .appendPath(params[1])
                .appendPath(params[2])
                .appendPath(params[3])
                .appendPath(params[4])
        val myUrl = builder.build().toString()
        val url = URL(myUrl)

        conn = openConnection(url)
        val response = conn.responseCode

        Log.i(TAG, "Url is: " + url)
        Log.i(TAG, "The response is: " + response)

        val inputStreamToWIMSPoint = InputStreamToWIMSPoint()
        val wimsPoints = inputStreamToWIMSPoint.readJsonStream(conn.inputStream)

        conn.disconnect()

        return wimsPoints
    }

    override fun onPostExecute(result: List<WIMSPoint>) {
        mDataViewActivity.progressSpinner.visibility = View.INVISIBLE
        listener.onTaskCompletedWIMSPoint(result)
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
