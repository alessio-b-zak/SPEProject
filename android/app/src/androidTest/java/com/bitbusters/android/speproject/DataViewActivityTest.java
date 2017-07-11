package com.bitbusters.android.speproject;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by mihajlo on 12/06/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DataViewActivityTest extends TestHelper {

    private static final String TAG = "DATA_VIEW_ACTIVITY_TEST";

    private static final String FOLDER_NAME = "DataViewActivityTest";

    private UiDevice mDevice;

//    @Rule
//    public ActivityTestRule<DataViewActivity> mActivityRule = new ActivityTestRule<>(DataViewActivity.class);
//
//    @Before
//    public void setActivity() {
//        // Start the activity
//        mActivityRule.getActivity();
//        // Access the device state using UiAutomator
//        mDevice = UiDevice.getInstance(getInstrumentation());
//    }
//
//    @Test
//    public void DataViewActivityClickInfoButton() {
////        onView(withId(R.id.info_button)).perform(click());
//        takeScreenshot(mDevice, FOLDER_NAME, "DataViewActivityClickInfoButton");
//        onView(withId(R.id.info_title)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void DataViewActivityClickGPSButton() {
//        clickButtonAndPause(GPS_BUTTON);
//        takeScreenshot(mDevice, FOLDER_NAME, "DataViewActivityClickGPSButton");
//    }
//
//    @Test
//    public void DataViewActivityClickSamplingPointButton() {
//        clickButtonAndPause(GPS_BUTTON);
//        clickButtonAndPause(SAMPLING_POINT_BUTTON);
//        UiObject samplingPointMarker = mDevice.findObject(new UiSelector().descriptionContains(SAMPLING_POINT_MARKER));
//        takeScreenshot(mDevice, FOLDER_NAME, "DataViewActivityClickSamplingPointButton");
//        assertTrue(samplingPointMarker.exists());
//    }
//
//    @Test
//    public void DataViewActivityClickSamplingPointMarker() {
//        clickButtonAndPause(GPS_BUTTON);
//        clickButtonAndPause(SAMPLING_POINT_BUTTON);
//        clickMarker(mDevice, SAMPLING_POINT_MARKER);
//        takeScreenshot(mDevice, FOLDER_NAME, "DataViewActivityClickSamplingPointMarker");
//        onView(withId(R.id.wims_name)).check(matches(isDisplayed()));
//        onView(withId(R.id.dataview_toolbar)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void SPDataViewFragmentClickGridViewButton() {
//        openSamplingPointDataViewFragment();
////        onView(withId(R.id.grid_view_button)).perform(click());
//        takeScreenshot(mDevice, FOLDER_NAME, "SPDataViewFragmentClickGridViewButton");
//        onView(withId(R.id.grid_view)).check(matches(isDisplayed()));
//        onView(withId(R.id.dataview_toolbar)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void SPDataViewFragmentClickSamplePointViewButton() {
//        openSamplingPointDataViewFragment();
////        onView(withId(R.id.grid_view_button)).perform(click());
////        onView(withId(R.id.map_view_button)).perform(click());
//        takeScreenshot(mDevice, FOLDER_NAME, "SPDataViewFragmentClickSamplePointViewButton");
//        onView(withId(R.id.grid_view)).check(matches(not(isDisplayed())));
//        onView(withId(R.id.dataview_toolbar)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void SPDataViewFragmentClickImageMarker() {
//        openSamplingPointDataViewFragment();
//        clickMarker(mDevice, PICTURE_MARKER);
//        takeScreenshot(mDevice, FOLDER_NAME, "SPDataViewFragmentClickImageMarker");
//        onView(withId(R.id.photo)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void SPDataViewFragmentClickBackButton() {
//        openSamplingPointDataViewFragment();
//        onView(withId(R.id.back_button_sp_data_view)).perform(click());
//        takeScreenshot(mDevice, FOLDER_NAME, "SPDataViewFragmentClickBackButton");
//        onView(withId(R.id.cam_button)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void PhotoViewFragmentClickBackButton() {
//        openSamplingPointDataViewFragment();
//        clickMarker(mDevice, PICTURE_MARKER);
//        onView(withId(R.id.back_button_photo_view)).perform(click());
//        takeScreenshot(mDevice, FOLDER_NAME, "PhotoViewFragmentClickBackButton");
//        onView(withId(R.id.wims_name)).check(matches(isDisplayed()));
//        onView(withId(R.id.dataview_toolbar)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void InfoViewFragmentClickBackButton() {
////        onView(withId(R.id.info_button)).perform(click());
//        onView(withId(R.id.back_button_info_view)).perform(click());
//        takeScreenshot(mDevice, FOLDER_NAME, "InfoViewFragmentClickBackButton");
//        onView(withId(R.id.cam_button)).check(matches(isDisplayed()));
//        onView(withId(R.id.gps_button)).check(matches(isDisplayed()));
//    }
//
//    public void openSamplingPointDataViewFragment() {
//        clickButtonAndPause(GPS_BUTTON);
//        clickButtonAndPause(SAMPLING_POINT_BUTTON);
//        clickMarker(mDevice, SAMPLING_POINT_MARKER);
//    }
}
