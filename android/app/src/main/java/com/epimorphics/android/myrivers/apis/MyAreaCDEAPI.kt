package com.epimorphics.android.myrivers.apis

import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.epimorphics.android.myrivers.activities.DataViewActivity
import com.epimorphics.android.myrivers.data.MyArea
import com.epimorphics.android.myrivers.interfaces.OnTaskCompleted
import com.epimorphics.android.myrivers.models.InputStreamToMyAreaCDE
import com.google.android.gms.maps.model.LatLng

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
/**
 * A class handling a query requesting MyArea waterbody data from the CDE API. Call is
 * made asynchronously in the background.
 *
 * @see MyArea
 */
open class MyAreaCDEAPI(private val listener: OnTaskCompleted) :
        AsyncTask<Any, Void, MyArea>() {

    val mDataViewActivity = listener as DataViewActivity
    private val TAG = "MY_AREA_API"
    lateinit var conn: HttpURLConnection
    private lateinit var myArea: MyArea

    /**
     * Builds uri, opens an http connection, makes a get request and returns parsed result.
     * All done in the background.
     *
     * @param params Any containing current location of the user and MyArea for which waterbody
     *               data is to be populated
     * @return MyArea with waterbody details populated
     */
    override fun doInBackground(vararg params: Any): MyArea {
        val myLocation = params[0] as Location
        myArea = params[1] as MyArea
        // Creates a miniature polygon around users location and uses it as a parameter to the
        // geolocation CDE API call
        val topLeft = LatLng(myLocation.latitude + 0.0005, myLocation.longitude - 0.0005)
        val topRight = LatLng(myLocation.latitude + 0.0005, myLocation.longitude + 0.0005)
        val botRight = LatLng(myLocation.latitude - 0.0005, myLocation.longitude + 0.0005)
        val botLeft = LatLng(myLocation.latitude - 0.0005, myLocation.longitude - 0.0005)

        // Builds an URI
        val builder = Uri.Builder()
        builder.scheme("http")
                .authority("ea-cde-pub.epimorphics.net")
                .appendPath("catchment-planning")
                .appendPath("so")
                .appendPath("WaterBody.json")
                .appendQueryParameter("polygon", "{\"type\":\"Polygon\",\"coordinates\":[[["
                        + topLeft.longitude + "," + topLeft.latitude + "],["
                        + topRight.longitude + "," + topRight.latitude + "],["
                        + botRight.longitude + "," + botRight.latitude + "],["
                        + botLeft.longitude + "," + botLeft.latitude + "],["
                        + topLeft.longitude + "," + topLeft.latitude + "]]]}")
                .appendQueryParameter("type", "River")
                .appendQueryParameter("_limit", "1")
        val myUrl = builder.build().toString()
        val url = URL(myUrl)
        conn = openConnection(url)
        val response = conn.responseCode

        Log.d(TAG, "Url is: " + url)
        Log.d(TAG, "The response is: " + response)

        // Parses the response
        val inputStream = conn.inputStream
        val inputStreamToMyArea = InputStreamToMyAreaCDE()
        inputStreamToMyArea.readJsonStream(inputStream, myArea)

        return myArea
    }

    /**
     * Called when doInBackground finishes executing. Communicates the result to the listener.
     *
     * @param result MyArea with waterbody data populated
     */
    override fun onPostExecute(result: MyArea) {
        listener.onTaskCompletedMyAreaCDE()
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
