package com.epimorphics.android.myrivers.apis

import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.epimorphics.android.myrivers.activities.DataViewActivity
import com.epimorphics.android.myrivers.data.DischargePermitPoint
import com.epimorphics.android.myrivers.interfaces.OnTaskCompleted
import com.epimorphics.android.myrivers.models.InputStreamToDischargePermit

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * A class handling a query requesting DischargePermitPoints from the application server. Call is
 * made asynchronously in the background.
 *
 * @see DischargePermitPoint
 */
open class DischargePermitPointAPI(private val listener: OnTaskCompleted) :
        AsyncTask<String, Void, List<DischargePermitPoint>>() {

    val mDataViewActivity = listener as DataViewActivity
    lateinit var conn: HttpURLConnection

    private val TAG = "WATER_DISCHARGE_API"

    /**
     * Builds uri, opens an http connection, makes a get request and returns parsed result.
     * All done in the background.
     *
     * @param params String locations of top left and bottom right corner of the screen
     * @return List<DischargePermitPoint> returned from the API call
     */
    override fun doInBackground(vararg params: String): List<DischargePermitPoint> {
        // Builds an URI
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

        // Parses the response
        val inputStream = conn.inputStream
        val inputStreamToWaterDischargePermit = InputStreamToDischargePermit()
        val dischargePermitPointList = inputStreamToWaterDischargePermit.readJsonStream(inputStream)

        conn.disconnect()

        return dischargePermitPointList
    }

    /**
     * Called when doInBackground finishes executing. Updates progress spinner visibility and
     * communicates the result to the listener.
     *
     * @param result List<DischargePermitPoint> returned from the API call
     */
    override fun onPostExecute(result: List<DischargePermitPoint>) {
        mDataViewActivity.progressSpinner.visibility = View.INVISIBLE
        listener.onTaskCompletedDischargePermitPoint(result)
    }

    /**
     * Opens an http connection, sets accept header and makes a GET request
     *
     * @param url URL to which to connect
     * @return HttpURLConnection connection
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun openConnection(url: URL): HttpURLConnection {
        conn = url.openConnection() as HttpURLConnection
        conn.readTimeout = 10000
        conn.connectTimeout = 15000
        conn.requestMethod = "GET"
        conn.doInput = true
        conn.instanceFollowRedirects = true
        conn.setRequestProperty("Accept", "application/json")

        HttpURLConnection.setFollowRedirects(true)
        conn.connect()

        return conn
    }

}
