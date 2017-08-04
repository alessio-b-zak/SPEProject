package com.bitbusters.android.speproject.apis

import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.bitbusters.android.speproject.activities.DataViewActivity
import com.bitbusters.android.speproject.data.DischargePermitPoint
import com.bitbusters.android.speproject.interfaces.OnTaskCompleted
import com.bitbusters.android.speproject.models.InputStreamToDischargePermit

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

open class DischargePermitPointAPI(private val listener: OnTaskCompleted) :
        AsyncTask<String, Void, List<DischargePermitPoint>>() {

    val mDataViewActivity = listener as DataViewActivity
    lateinit var conn: HttpURLConnection

    private val TAG = "WATER_DISCHARGE_API"

    override fun doInBackground(vararg params: String): List<DischargePermitPoint> {
        val builder = Uri.Builder()
        builder.scheme("http")
                .encodedAuthority("139.59.184.70:8080")
                //.encodedAuthority("172.23.215.243:3000")
                .appendPath("getPermits")
                .appendPath(params[0])
                .appendPath(params[1])
                .appendPath(params[2])
                .appendPath(params[3])
        val myUrl = builder.build().toString()
        val url = URL(myUrl)

        conn = openConnection(url)
        val response = conn.responseCode

        Log.i(TAG, "Url is: " + url)
        Log.i(TAG, "The response is: " + response)

        val inputStream = conn.inputStream
        val inputStreamToWaterDischargePermit = InputStreamToDischargePermit()
        val dischargePermitPointList = inputStreamToWaterDischargePermit.readJsonStream(inputStream)

        conn.disconnect()

        return dischargePermitPointList
    }

    override fun onPostExecute(result: List<DischargePermitPoint>) {
        mDataViewActivity.progressSpinner.visibility = View.INVISIBLE
        listener.onTaskCompletedDischargePermitPoint(result)
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
