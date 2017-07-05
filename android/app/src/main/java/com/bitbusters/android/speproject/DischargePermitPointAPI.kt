package com.bitbusters.android.speproject

import android.net.Uri
import android.os.AsyncTask
import android.provider.ContactsContract
import android.util.Log
import android.view.View

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

open class DischargePermitPointAPI(private val listener: OnTaskCompleted) :
        AsyncTask<String, Void, List<DischargePermitPoint>>() {

    val mDataViewActivity = listener as DataViewActivity;

    companion object {
        private val TAG = "WATER_DISCHARGE_API"
        private val EFFLUENT_TYPE_URI = "http://environment.data.gov.uk/public-register/water-discharges/def/effluent-type/"
    }

    override fun doInBackground(vararg params: String): List<DischargePermitPoint> {
        val easting = params[0]
        val northing = params[1]
        val distance = params[2]
        val effluentType = EFFLUENT_TYPE_URI + params[3]
        var dischargePermitPointList: List<DischargePermitPoint> = arrayListOf()

        val builder = Uri.Builder()
        builder.scheme("http")
                .authority("environment.data.gov.uk")
                .appendPath("public-register")
                .appendPath("water-discharges")
                .appendPath("registration.json")
                .appendQueryParameter("easting", easting)
                .appendQueryParameter("northing", northing)
                .appendQueryParameter("dist", distance)
                .appendQueryParameter("effluentType", effluentType)
                .appendQueryParameter("_limit", "500")
        val myUrl = builder.build().toString()
        var url = URL(myUrl)

        var conn = openConnection(url)
        var response = conn!!.responseCode

        while (response in 300..399) {
            val redirectUrl = conn!!.getHeaderField("Location")
            Log.i(TAG, "Redirect URL: " + redirectUrl)
            url = URL(redirectUrl)
            // open the new connection again
            conn = openConnection(url)
            response = conn!!.responseCode
        }

        Log.i(TAG, "Url is: " + url)
        Log.i(TAG, "The response is: " + response)

        val inputStream = conn!!.inputStream
        val inputStreamToWaterDischargePermit = InputStreamToWaterDischargePermit()
        dischargePermitPointList = inputStreamToWaterDischargePermit.readJsonStream(inputStream)

        conn.disconnect()

        return dischargePermitPointList
    }

    override fun onPostExecute(result: List<DischargePermitPoint>) {
        mDataViewActivity.progressSpinner.visibility = View.INVISIBLE
        listener.onTaskCompletedDischargePermitPoint(result)
    }

    private fun openConnection(url: URL): HttpURLConnection? {
        var conn: HttpURLConnection? = null
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
