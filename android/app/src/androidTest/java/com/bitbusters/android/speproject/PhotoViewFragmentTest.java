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
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by mihajlo on 12/06/17.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PhotoViewFragmentTest {

    private static final String TAG = "IMG_VIEW_FRAGMENT_TEST";
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
        // Search for a Single Image Marker
        UiObject imageMarker = mDevice.findObject(new UiSelector().descriptionContains("Picture_Point."));
        // Try and Click the Image Marker (TopLeft because image might overlap with current location)
        try {
            imageMarker.clickTopLeft();
            Log.i(TAG, "Image Marker Clicked");
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, "No Image Marker Displayed");
        }
    }

    @Test
    public void test0_clickBackButton() {
        // Click Back Button
        onView(withId(R.id.back_button_photo_view)).perform(click());
        // Check that Grid View Button Reappeared
        onView(withId(R.id.grid_view_button)).check(matches(isDisplayed()));
    }
}
