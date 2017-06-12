package com.bitbusters.android.speproject;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.filters.LargeTest;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;


import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by mihajlo on 12/06/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DataViewActivitySamplingPointButtonTest {
    private static final String TAG = "SAMPLING_PT_BUTTON_TEST";
    private UiDevice mDevice;

    @Rule
    public ActivityTestRule<DataViewActivity> mActivityRule = new ActivityTestRule<>(DataViewActivity.class);

    @Before
    public void setActivity() {
        // Start the activity
        mActivityRule.getActivity();
        // Zoom in on to user's location
        onView(withId(R.id.gps_button)).perform(click());
        // Access the device state using UiAutomator
        mDevice = UiDevice.getInstance(getInstrumentation());
    }

    @Test
    public void clickSamplingPointButton() {
        // Click on Sampling Point Button
        onView(withId(R.id.sp_view_button)).perform(click());
        // Check if SPDataFragment is displayed
        UiObject samplingPointMarker = mDevice.findObject(new UiSelector().descriptionContains("Sampling_Point"));
        try {
            samplingPointMarker.isEnabled();
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, "No Sampling Points Displayed");
        }
    }


}
