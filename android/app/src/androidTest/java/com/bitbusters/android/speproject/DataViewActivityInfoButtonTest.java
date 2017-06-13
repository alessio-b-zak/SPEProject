package com.bitbusters.android.speproject;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
public class DataViewActivityInfoButtonTest {

    @Rule
    public ActivityTestRule<DataViewActivity> mActivityRule = new ActivityTestRule<>(DataViewActivity.class);

    @Before
    public void setActivity() {
        // Start the activity
        mActivityRule.getActivity();
    }

    @Test
    public void test0_clickInfoButton() {
        // Click on Info Button
        onView(withId(R.id.info_button)).perform(click());
        // Check if Info Fragment is displayed
        onView(withId(R.id.info_title)).check(matches(isDisplayed()));
    }

}
