package com.bitbusters.android.speproject.apis

import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.util.JsonReader
import android.util.Log
import com.bitbusters.android.speproject.activities.DataViewActivity
import com.bitbusters.android.speproject.data.DischargePermitPoint
import com.bitbusters.android.speproject.interfaces.OnTaskCompleted
import com.bitbusters.android.speproject.models.InputStreamToDischargePermit
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

open class MyAreaNearestPermitAPI(private val listener: OnTaskCompleted) :
        AsyncTask<Location, Void, DischargePermitPoint>() {

    val mDataViewActivity = listener as DataViewActivity
    private val TAG = "AREA_NEAREST_PERMIT_API"
    lateinit var conn: HttpURLConnection

    override fun doInBackground(vararg params: Location): DischargePermitPoint {
        val myLocation: Location = params[0]

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

        val inputStream = conn.inputStream
        val inputStreamToDischargePermit = InputStreamToDischargePermit()
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        val result = inputStreamToDischargePermit.readMessageWithDistance(reader)

        return result
    }

    // onPostExecute displays the results of the AsyncTask.
    override fun onPostExecute(result: DischargePermitPoint) {
//        mMyAreaFragment.populatePermitData(result)
        listener.onTaskCompletedMyAreaPermit(result)
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
