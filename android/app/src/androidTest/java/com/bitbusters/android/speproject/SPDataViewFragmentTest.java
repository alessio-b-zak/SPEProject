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
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mihajlo on 12/06/17.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SPDataViewFragmentTest {

    private static final String TAG = "SP_DATA_FRAGMENT_TEST";
    private static final String PACKAGE = "com.bitbusters.android.speproject";
    private static final String ID = ":id/";
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
        // Search for a Single Sample Point Marker
        UiObject samplePointMarker = mDevice.findObject(new UiSelector().descriptionContains("Sample_Point."));
        // Try and Click the Sampling Point Marker
        try {
            samplePointMarker.click();
            Log.i(TAG, "Sampling Point Marker Clicked");
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, "No Sampling Point Marker Displayed");
        }
    }

    @Test
    public void test0_clickGridViewButton() {
        // Search for the Grid View Button
        UiObject gridViewButton = mDevice.findObject(new UiSelector().resourceId(PACKAGE + ID + "grid_view_button"));
        // Try and Click on Grid View Button
        try {
            gridViewButton.click();
            Log.i(TAG, "Grid View Button Clicked");
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, "Grid View Button Not Found");
        }
        // Search for the Grid View Background
        UiObject gridView = mDevice.findObject(new UiSelector().resourceId(PACKAGE + ID + "grid_view"));
        // Check if Grid View Background exists
        assertTrue(gridView.exists());
    }

    @Test
    public void test1_clickSamplePointViewButton() {
        // Search for the Sample Point View Button
        UiObject mapViewButton = mDevice.findObject(new UiSelector().resourceId(PACKAGE + ID + "map_view_button"));
        // Try and Click on Sample Point View Button
        try {
            mapViewButton.click();
            Log.i(TAG, "Map View Button Clicked");
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, "Map View Button Not Found");
        }
        // Search for the Grid View Background
        UiObject gridView = mDevice.findObject(new UiSelector().resourceId(PACKAGE + ID + "grid_view"));
        // Check if Grid View Background DOESN'T exist
        assertFalse(gridView.exists());
    }

    @Test
    public void test2_clickImageMarker() {
        // Search for a Single Image Marker
        UiObject imageMarker = mDevice.findObject(new UiSelector().descriptionContains("Picture_Point"));
        // Try and Click the Image Marker
        try {
            imageMarker.click();
            Log.i(TAG, "Image Marker Clicked");
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, "No Image Marker Displayed");
        }
        // Search for the Photo View
        UiObject photoView = mDevice.findObject(new UiSelector().resourceId(PACKAGE + ID + "photo"));
        // Check if Photo View exists
        assertTrue(photoView.exists());
    }

    @Test
    public void test3_clickBackButton() {
        // Search for a Back Button
        UiObject backButton = mDevice.findObject(new UiSelector().resourceId(PACKAGE + ID + "back_button"));
        // Try and Click the Back Button
        try {
            backButton.click();
            Log.i(TAG, "Back Button Clicked");
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, "Back Button not Found");
        }
        // Search for Sample Point Name in the Sampling Point Data View Fragment
        UiObject samplePointName = mDevice.findObject(new UiSelector().resourceId(PACKAGE + ID + "sp_name"));
        // Check if Sample Point Name DOESN'T exists
        assertFalse(samplePointName.exists());
    }
}
