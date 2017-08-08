package com.epimorphics.android.myrivers

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.epimorphics.android.myrivers.activities.DataViewActivity

/**
 * Created by mihajlo on 28/07/17.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class InfoFragmentTest: TestHelper() {
    private val TAG = "INFO_FRAGMENT_TEST"

    private val FOLDER_NAME = "InfoFragmentTest"

    private lateinit var mDevice: UiDevice

    @Rule @JvmField
    val mActivityRule = ActivityTestRule<DataViewActivity>(DataViewActivity::class.java)

    @Before
    fun setActivity() {
        // Start the activity
        mActivityRule.activity
        // Access the device state using UiAutomator
        mDevice = UiDevice.getInstance(getInstrumentation())
        // Open info fragment
        onView(withId(R.id.data_view_hamburger_button)).perform(click())
        findObjectByText(mDevice, R.string.drawer_info).click()
    }

    @Test
    fun InfoFragmentClickBackButton() {
        // Action
        onView(withId(R.id.back_button_info_view)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "InfoFragmentClickBackButton")
        // Tests
        onView(withId(R.id.data_view_hamburger_button)).check(matches(isDisplayed()))
        onView(withId(R.id.data_view_search_button)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }
}