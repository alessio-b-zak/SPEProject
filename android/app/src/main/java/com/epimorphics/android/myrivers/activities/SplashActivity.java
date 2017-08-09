package com.epimorphics.android.myrivers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
/**
 * A first activity loaded when the application is opened.
 * It shows a logo of the app on the standard background.
 */
public class SplashActivity extends Activity {

    /**
     * Called when a fragment is created. Creates an intent and opens DataViewActivity.
     *
     * @param savedInstanceState Saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(SplashActivity.this, DataViewActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }



}
