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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mihajlo on 12/06/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DataViewActivityGPSButtonTest {

    @Rule
    public ActivityTestRule<DataViewActivity> mActivityRule = new ActivityTestRule<>(DataViewActivity.class);

    @Before
    public void setActivity() {
        // Start the activity
        mActivityRule.getActivity();
    }

    @Test
    public void test0_clickGPSButton() {
        // Click on GPS Button
        onView(withId(R.id.gps_button)).perform(click());
        // Sleep for 1s to allow Google Map to transition to the location
        SystemClock.sleep(1000);
        // TODO: Take a screenshot
    }

}
