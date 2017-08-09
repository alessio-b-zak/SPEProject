package com.epimorphics.android.myrivers.apis

import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.util.JsonReader
import android.util.Log
import com.epimorphics.android.myrivers.activities.DataViewActivity
import com.epimorphics.android.myrivers.data.DischargePermitPoint
import com.epimorphics.android.myrivers.data.MyArea
import com.epimorphics.android.myrivers.interfaces.OnTaskCompleted
import com.epimorphics.android.myrivers.models.InputStreamToDischargePermit
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * A class handling a query requesting MyArea nearest DischargePermitPoint from the application
 * server. Call is made asynchronously in the background.
 *
 * @see MyArea
 */
open class MyAreaNearestPermitAPI(private val listener: OnTaskCompleted) :
        AsyncTask<Location, Void, DischargePermitPoint>() {

    val mDataViewActivity = listener as DataViewActivity
    private val TAG = "AREA_NEAREST_PERMIT_API"
    lateinit var conn: HttpURLConnection

    /**
     * Builds uri, opens an http connection, makes a get request and returns parsed result.
     * All done in the background.
     *
     * @param params Location a current location of the user
     * @return DischargePermitPoint a nearest DischargePermitPoint to the users location
     */
    override fun doInBackground(vararg params: Location): DischargePermitPoint {
        val myLocation: Location = params[0]

        // Builds an URI
        val builder = Uri.Builder()
        builder.scheme("http")
                .encodedAuthority("139.59.184.70:8080")
                //.encodedAuthority("172.23.215.243:3000")
                .appendPath("getNearestPermit")
                .appendPath(myLocation.latitude.toString())
                .appendPath(myLocation.longitude.toString())
        val myUrl = builder.build().toString()
        val url = URL(myUrl)
        conn = openConnection(url)
        val response = conn.responseCode

        Log.i(TAG, "Url is: " + url)
        Log.i(TAG, "The response is: " + response)

        // Parses the response
        val inputStream = conn.inputStream
        val inputStreamToDischargePermit = InputStreamToDischargePermit()
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        val result = inputStreamToDischargePermit.readMessageWithDistance(reader)

        return result
    }

    /**
     * Called when doInBackground finishes executing. Communicates the result to the listener.
     *
     * @param result DischargePermitPoint a nearest DischargePermitPoint to the users location
     */
    override fun onPostExecute(result: DischargePermitPoint) {
        listener.onTaskCompletedMyAreaPermit(result)
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
