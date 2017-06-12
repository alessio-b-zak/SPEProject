package com.bitbusters.android.speproject;

import android.os.SystemClock;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SamplingPointDataViewFragmentTest {
    private static final String TAG = "SAMPLING_PT_MARKER_TEST";
    private UiDevice mDevice;

    @Rule
    public ActivityTestRule<DataViewActivity> mActivityRule = new ActivityTestRule<>(DataViewActivity.class);

    @Before
    public void setActivity() {
        // Start the activity
        mActivityRule.getActivity();
        // Zoom in on to user's location
        onView(withId(R.id.gps_button)).perform(click());
        // Sleep for 1s to allow Google Map to transition to the location
        SystemClock.sleep(1000);
        // Display all sampling points in the area
        onView(withId(R.id.sp_view_button)).perform(click());
        // Access the device state using UiAutomator
        mDevice = UiDevice.getInstance(getInstrumentation());
    }

    @Test
    public void test0_clickSamplingPointMarker() {
        // Click on a single Marker
        UiObject samplePointMarker = mDevice.findObject(new UiSelector().descriptionContains("Sampling_Point"));
        try {
            samplePointMarker.click();
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, "No Sampling Point Marker Displayed");
        }
        // Check if SPDataFragment is displayed
        UiObject samplePointName = mDevice.findObject(new UiSelector().resourceId("sp_name"));
        try {
            samplePointName.isEnabled();
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, "Fail to load SPDataView");
        }
    }

    @Test
    public void test1_clickGridViewButton() {

    }

    @Test
    public void test2_clickSamplePointButton() {

    }

    @Test
    public void test3_clickImageMarker() {

    }

    @Test
    public void test4_clickBackButton() {

    }
}
