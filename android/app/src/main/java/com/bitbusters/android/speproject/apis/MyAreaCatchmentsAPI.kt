package com.bitbusters.android.speproject.apis

import android.os.AsyncTask
import android.util.Log
import com.bitbusters.android.speproject.data.MyArea
import com.bitbusters.android.speproject.models.InputStreamToMyAreaCatchments

import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

open class MyAreaCatchmentsAPI() :
        AsyncTask<MyArea, Void, Unit>() {

    private val TAG = "MY_AREA_CATCHMENTS_API"
    lateinit var conn: HttpURLConnection

    override fun doInBackground(vararg params: MyArea) {
        val myArea = params[0]

        val url = URL(myArea.operationalCatchment + ".json")
        conn = openConnection(url)

        val response = conn.responseCode
        Log.d(TAG, "Url is: " + url)
        Log.d(TAG, "The response is: " + response)

        val inputStream = conn.inputStream
        val inputStreamToMyAreaCatchments = InputStreamToMyAreaCatchments()

        inputStreamToMyAreaCatchments.readJsonStream(inputStream, myArea)
    }

    // onPostExecute displays the results of the AsyncTask.
//    override fun onPostExecute() {
////        mMyAreaFragment.populateCDEData(result)
////        listener.onTaskCompletedMyAreaCatchment(result)
//    }


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
