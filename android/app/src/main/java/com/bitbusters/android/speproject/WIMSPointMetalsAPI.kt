package com.bitbusters.android.speproject

import android.net.Uri
import android.os.AsyncTask
import android.util.Log

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

open class WIMSPointMetalsAPI(private val mWIMSDataFragment: WIMSDataFragment) :
        AsyncTask<WIMSPoint, Void, WIMSPoint>() {

    lateinit var conn : HttpURLConnection

    companion object {
        private val TAG = "WIMS_METALS_API"
    }

    override fun doInBackground(vararg params: WIMSPoint): WIMSPoint {
        val wimsPoint = params[0]

        val builder = Uri.Builder()
        builder.scheme("http")
                .authority("environment.data.gov.uk")
                .appendPath("water-quality")
                .appendPath("data")
                .appendPath("measurement.json")
                .appendQueryParameter("samplingPoint", wimsPoint.id)
                .appendQueryParameter("determinandGroup", "metals")
                .appendQueryParameter("_limit", "5000")
                .appendQueryParameter("_sort", "-sample")
        val myUrl = builder.build().toString()
        val url = URL(myUrl)

        conn = openConnection(url)
        val response = conn.responseCode

        Log.i(TAG, "Url is: " + url)
        Log.i(TAG, "The response is: " + response)

        val inputStream = conn.inputStream
        val inputStreamToWIMSMeasurements = InputStreamToWIMSMeasurements()
        inputStreamToWIMSMeasurements.readJsonStream(wimsPoint, inputStream)

        conn.disconnect()
//
//        Log.i(TAG, wimsPoint.measurementMap.toString())

        return wimsPoint
    }

    override fun onPostExecute(result: WIMSPoint) {
        mWIMSDataFragment.showMoreInfoButton()
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
