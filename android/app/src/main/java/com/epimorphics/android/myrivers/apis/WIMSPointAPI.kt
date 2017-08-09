package com.epimorphics.android.myrivers.apis

import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.epimorphics.android.myrivers.activities.DataViewActivity
import com.epimorphics.android.myrivers.data.WIMSPoint
import com.epimorphics.android.myrivers.interfaces.OnTaskCompleted
import com.epimorphics.android.myrivers.models.InputStreamToWIMSPoint
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * A class handling a query requesting WimsPoints from the application server. Call is
 * made asynchronously in the background.
 *
 * @see WIMSPoint
 */
open class WIMSPointAPI(private val listener: OnTaskCompleted) :
        AsyncTask<String, Void, List<WIMSPoint>>() {

    val mDataViewActivity = listener as DataViewActivity
    lateinit var conn: HttpURLConnection

    private val TAG = "WIMS_POINTS_API"

    /**
     * Builds uri, opens an http connection, makes a get request and returns parsed result.
     * All done in the background.
     *
     * @param params String locations of top left and bottom right corner of the screen
     * @return List<WIMSPoint> returned from the API call
     */
    override fun doInBackground(vararg params: String): List<WIMSPoint> {
        // Builds an URI
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

        // Parses the response
        val inputStreamToWIMSPoint = InputStreamToWIMSPoint()
        val wimsPoints = inputStreamToWIMSPoint.readJsonStream(conn.inputStream)

        conn.disconnect()

        return wimsPoints
    }

    /**
     * Called when doInBackground finishes executing. Updates progress spinner visibility and
     * communicates the result to the listener.
     *
     * @param result List<WIMSPoint> returned from the API call
     */
    override fun onPostExecute(result: List<WIMSPoint>) {
        mDataViewActivity.progressSpinner.visibility = View.INVISIBLE
        listener.onTaskCompletedWIMSPoint(result)
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
