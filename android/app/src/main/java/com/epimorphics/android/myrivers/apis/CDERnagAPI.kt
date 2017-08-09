package com.epimorphics.android.myrivers.apis

import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.epimorphics.android.myrivers.data.CDEPoint
import com.epimorphics.android.myrivers.data.RNAG
import com.epimorphics.android.myrivers.fragments.CDEDataFragment
import com.epimorphics.android.myrivers.models.InputStreamToRNAG
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * A class handling a query requesting CDEPoint RNAGs from the CDE API. Call is made
 * asynchronously in the background.
 *
 * @see CDEPoint
 * @see RNAG
 */
open class CDERnagAPI(private val mCDEDataFragment: CDEDataFragment) :
        AsyncTask<CDEPoint, Void, CDEPoint>() {

    lateinit var conn: HttpURLConnection

    companion object {
        private val TAG = "CDE_RNAG_API"
    }

    /**
     * Builds uri, opens an http connection, makes a get request and returns parsed result.
     * All done in the background.
     *
     * @param params CDEPoint for which RNAGs are to be populated
     * @return CDEPoint with RNAGs populated
     */
    override fun doInBackground(vararg params: CDEPoint): CDEPoint {
        val cdePoint = params[0]

        // Builds an URI
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

        // Parses the response
        val inputStream = conn.inputStream
        val inputStreamToRNAG = InputStreamToRNAG()
        inputStreamToRNAG.readJsonStream(cdePoint, inputStream)

        conn.disconnect()

        return cdePoint
    }

    /**
     * Called when doInBackground finishes executing. Sends the result back to the CDEDataFragment.
     *
     * @param result CDEPoint with RNAGs populated
     */
    override fun onPostExecute(result: CDEPoint) {
        mCDEDataFragment.classificationPopulated(CDEPoint.RNAG)
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
