package com.epimorphics.android.myrivers.apis

import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.epimorphics.android.myrivers.data.WIMSPoint
import com.epimorphics.android.myrivers.fragments.WIMSDataFragment
import com.epimorphics.android.myrivers.models.InputStreamToWIMSMeasurements

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

open class WIMSPointRatingsAPI(private val mWIMSDataFragment: WIMSDataFragment) :
        AsyncTask<WIMSPoint, Void, WIMSPoint>() {

    lateinit var conn: HttpURLConnection

    companion object {
        private val TAG = "WIMS_RATINGS_API"
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
                .appendQueryParameter("determinand", "0076")
                .appendQueryParameter("determinand", "0077")
                .appendQueryParameter("determinand", "0061")
                .appendQueryParameter("determinand", "0092")
                .appendQueryParameter("determinand", "0085")
                .appendQueryParameter("determinand", "0117")
                .appendQueryParameter("determinand", "9853")
                .appendQueryParameter("determinand", "0192")
                .appendQueryParameter("determinand", "0180")
                .appendQueryParameter("determinand", "9856")
                .appendQueryParameter("determinand", "0135")
                .appendQueryParameter("determinand", "0134")
                .appendQueryParameter("determinand", "1012")
                .appendQueryParameter("determinand", "0143")
                .appendQueryParameter("determinand", "9924")
                .appendQueryParameter("determinand", "9901")
                .appendQueryParameter("_limit", "5000")
                .appendQueryParameter("_sort", "-sample")
        val myUrl = builder.build().toString()
        var url = URL(myUrl)

        conn = openConnection(url)
        var response = conn.responseCode

        Log.i(TAG, "Url is: " + url)
        Log.i(TAG, "The response is: " + response)

        val inputStream = conn.inputStream
        val inputStreamToWIMSMeasurements = InputStreamToWIMSMeasurements()
        inputStreamToWIMSMeasurements.readJsonStream(wimsPoint, inputStream)

        conn.disconnect()

//        Log.i(TAG, wimsPoint.measurementMap.toString())

        return wimsPoint
    }

    override fun onPostExecute(result: WIMSPoint) {
        mWIMSDataFragment.setMeasurementsText(result)
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
