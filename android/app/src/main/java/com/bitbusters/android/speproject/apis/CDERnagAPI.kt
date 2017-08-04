package com.bitbusters.android.speproject.apis

import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.bitbusters.android.speproject.data.CDEPoint
import com.bitbusters.android.speproject.fragments.CDEDataFragment
import com.bitbusters.android.speproject.models.InputStreamToRNAG
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

open class CDERnagAPI(private val mCDEDataFragment: CDEDataFragment) :
        AsyncTask<CDEPoint, Void, CDEPoint>() {

    lateinit var conn: HttpURLConnection

    companion object {
        private val TAG = "CDE_RNAG_API"
    }

    override fun doInBackground(vararg params: CDEPoint): CDEPoint {
        val cdePoint = params[0]

        val builder = Uri.Builder()
        builder.scheme("http")
                .authority("ea-cde-pub.epimorphics.net")
                .appendPath("catchment-planning")
                .appendPath("data")
                .appendPath("reason-for-failure.json")
                .appendQueryParameter("waterBody", cdePoint.waterbodyId)
        val myUrl = builder.build().toString()
        val url = URL(myUrl)

        conn = openConnection(url)
        val response = conn.responseCode

        Log.i(TAG, "Url is: " + url)
        Log.i(TAG, "The response is: " + response)

        val inputStream = conn.inputStream
        val inputStreamToRNAG = InputStreamToRNAG()
        inputStreamToRNAG.readJsonStream(cdePoint, inputStream)

        conn.disconnect()

        return cdePoint
    }

    override fun onPostExecute(result: CDEPoint) {
        mCDEDataFragment.classificationPopulated(CDEPoint.RNAG)
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
