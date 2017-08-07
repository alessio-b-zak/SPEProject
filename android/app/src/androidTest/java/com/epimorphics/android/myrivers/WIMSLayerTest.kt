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

/**
 * Created by mihajlo on 28/07/17.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class WIMSLayerTest: TestHelper() {

    private val FOLDER_NAME = "WIMSLayerTest"

    private lateinit var mDevice: UiDevice

    @Rule @JvmField
    val mActivityRule = ActivityTestRule<DataViewActivity>(DataViewActivity::class.java)

    @Before
    fun setActivity() {
        // Start the activity
        mActivityRule.activity
        // Access the device state using UiAutomator
        mDevice = UiDevice.getInstance(getInstrumentation())
        // Zoom in to the current location
        onView(withId(R.id.gps_button)).perform(click())
        // Open info fragment
        onView(withId(R.id.data_view_hamburger_button)).perform(click())
        findObjectByText(mDevice, R.string.drawer_wims).click()
    }

    @Test
    fun WIMSLayerClickWIMSMarker() {
        // Action
        findObjectByDescriptor(mDevice, R.string.marker_wims).click()
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "WIMSLayerClickWIMSMarker")
        // Tests
        onView(withId(R.id.wims_name)).check(matches(isDisplayed()))
        onView(withId(R.id.wims_table)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun WIMSDataFragmentClickBackButton() {
        // Action
        openWIMSDataFragment()
        onView(withId(R.id.back_button_wims_data_view)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "WIMSDataFragmentClickBackButton")
        // Tests
        onView(withId(R.id.data_view_hamburger_button)).check(matches(isDisplayed()))
        onView(withId(R.id.data_view_search_button)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun WIMSDataFragmentClickMoreInfoButton() {
        // Action
        openWIMSDataFragment()
        onView(withId(R.id.info_button_wims_data_view)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "WIMSDataFragmentClickMoreInfoButton")
        // Tests
        onView(withId(R.id.wims_details_title)).check(matches(isDisplayed()))
        onView(withId(R.id.back_button_wims_details_view)).check(matches(isDisplayed()))
    }

    @Test
    fun WIMSDetailsFragmentClickBackButton() {
        // Action
        openWIMSDetailsFragment()
        onView(withId(R.id.back_button_wims_details_view)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "WIMSDetailsFragmentClickBackButton")
        // Tests
        onView(withId(R.id.wims_name)).check(matches(isDisplayed()))
        onView(withId(R.id.wims_table)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    fun openWIMSDataFragment() {
        findObjectByDescriptor(mDevice, R.string.marker_wims).click()
    }

    fun openWIMSDetailsFragment() {
        openWIMSDataFragment()
        onView(withId(R.id.info_button_wims_data_view)).perform(click())
    }
}