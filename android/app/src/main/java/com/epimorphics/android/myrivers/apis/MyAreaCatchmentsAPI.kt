package com.epimorphics.android.myrivers.apis

import android.os.AsyncTask
import android.util.Log
import com.epimorphics.android.myrivers.data.MyArea
import com.epimorphics.android.myrivers.models.InputStreamToMyAreaCatchments

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * A class handling a query requesting MyArea catchment data from the CDE API. Call is
 * made asynchronously in the background.
 *
 * @see MyArea
 */
open class MyAreaCatchmentsAPI :
        AsyncTask<MyArea, Void, Unit>() {

    private val TAG = "MY_AREA_CATCHMENTS_API"
    lateinit var conn: HttpURLConnection

    /**
     * Builds uri, opens an http connection, makes a get request and returns parsed result.
     * All done in the background.
     *
     * @param params MyArea for which catchment data is to be populated
     */
    override fun doInBackground(vararg params: MyArea) {
        val myArea = params[0]

        //Builds an URI
        val url = URL(myArea.operationalCatchment + ".json")
        conn = openConnection(url)
        val response = conn.responseCode

        Log.d(TAG, "Url is: " + url)
        Log.d(TAG, "The response is: " + response)

        // Parses the response
        val inputStream = conn.inputStream
        val inputStreamToMyAreaCatchments = InputStreamToMyAreaCatchments()

        inputStreamToMyAreaCatchments.readJsonStream(inputStream, myArea)
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
