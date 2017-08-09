package com.epimorphics.android.myrivers.apis;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.epimorphics.android.myrivers.data.CDEAsyncReturn;
import com.epimorphics.android.myrivers.data.CDEPoint;
import com.epimorphics.android.myrivers.fragments.CDEDataFragment;
import com.epimorphics.android.myrivers.models.InputStreamToCDEClassification;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.epimorphics.android.myrivers.data.CDEPoint.OBJECTIVE;
import static com.epimorphics.android.myrivers.data.CDEPoint.PREDICTED;
import static com.epimorphics.android.myrivers.data.CDEPoint.REAL;

/**
 * A class handling a query requesting CDEPoint ratings from the CDE API. Call is made
 * asynchronously in the background.
 *
 * @see CDEPoint
 */
public class CDEPointRatingsAPI extends AsyncTask<Object, Void, CDEAsyncReturn> {

    private static final String TAG = "CDE_GENERAL_RATINGS";
    private CDEDataFragment mCDEDataFragment;

    public CDEPointRatingsAPI(CDEDataFragment cdeDataFragment) {
        this.mCDEDataFragment = cdeDataFragment;
    }

    /**
     * Builds uri, opens an http connection, makes a get request and returns parsed result.
     * All done in the background.
     *
     * @param params Object containing a CDEPoint and a ratings group(Real, Predicted, Objective)
     * @return CDEAsyncReturn an object containing a CDEPoint and a corresponding Classification
     *
     * @see CDEAsyncReturn
     */
    @Override
    protected CDEAsyncReturn doInBackground(Object... params) {
        CDEPoint cdePoint = (CDEPoint) params[0];
        String group = (String) params[1];
        String linkExtension = setLinkExtension(group);
        try {
            // Builds an URI
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("ea-cde-pub.epimorphics.net")
                    .appendPath("catchment-planning")
                    .appendPath("data")
                    .appendPath("classification" + linkExtension + ".json")
                    .appendQueryParameter("waterBody", cdePoint.getWaterbodyId())
                    .appendQueryParameter("classificationItem", "wbc_1")
                    .appendQueryParameter("classificationItem", "wbc_2")
                    .appendQueryParameter("classificationItem", "wbc_106")
                    .appendQueryParameter("classificationItem", "wbc_13")
                    .appendQueryParameter("classificationItem", "wbc_5")
                    .appendQueryParameter("classificationItem", "wbc_228")
                    .appendQueryParameter("classificationItem", "wbc_6")
                    .appendQueryParameter("classificationItem", "wbc_7")
                    .appendQueryParameter("classificationItem", "wbc_8")
                    .appendQueryParameter("classificationItem", "wbc_10")
                    .appendQueryParameter("classificationItem", "wbc_229")
                    .appendQueryParameter("classificationItem", "wbc_9")
                    .appendQueryParameter("_sort", "-classificationYear")
                    .appendQueryParameter("_sort", "-cycle");

            String myUrl = builder.build().toString();
            URL url = new URL(myUrl);

            // Opens a connection and makes a GET request
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();

            Log.i(TAG, "Url is: " + url);
            Log.i(TAG, "The response is: " + response);

            // Parses the response
            InputStream inputStream = conn.getInputStream();
            InputStreamToCDEClassification inputStreamToCDEClassification = new InputStreamToCDEClassification();
            inputStreamToCDEClassification.readJsonStream(cdePoint, inputStream, group);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CDEAsyncReturn(cdePoint, group);
    }

    /**
     * Called when doInBackground finishes executing. Sends the result back to the CDEDataFragment.
     *
     * @param result CDEAsyncReturn returned from the API call
     */
    @Override
    protected void onPostExecute(CDEAsyncReturn result) {
        if (result.getGroup().equals(REAL)) {
            mCDEDataFragment.setClassificationText(result.getCdePoint());
        } else {
            mCDEDataFragment.classificationPopulated(result.getGroup());
        }
    }

    /**
     * Given a classification group name it returns a link extension used when by URI builder.
     *
     * @param group a classification group name
     * @return linkExtension
     */
    private String setLinkExtension(String group) {
        String linkExtension = "";
        switch (group) {
            case OBJECTIVE:
                linkExtension = "-objective-outcome";
                break;
            case PREDICTED:
                linkExtension = "-predicted-outcome";
                break;
            default:
                break;
        }
        return linkExtension;
    }
}
