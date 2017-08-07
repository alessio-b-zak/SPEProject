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
class CDELayerTest: TestHelper() {

    private val FOLDER_NAME = "CDELayerTest"

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
        clickButtonAndPause(R.id.gps_button)
    }

    @Test
    fun CDELayerClickGeoJsonShape() {
        // Action
        findObjectByDescriptor(mDevice, R.string.google_map).clickBottomRight()
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "CDELayerClickGeoJsonShape")
        // Tests
        onView(withId(R.id.cde_label)).check(matches(isDisplayed()))
        onView(withId(R.id.cde_table)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun CDEDataFragmentClickBackButton() {
        // Action
        openCDEDataFragment()
        onView(withId(R.id.back_button_cde_data_view)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "CDEDataFragmentClickBackButton")
        // Tests
        onView(withId(R.id.data_view_hamburger_button)).check(matches(isDisplayed()))
        onView(withId(R.id.data_view_search_button)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun CDEDataFragmentClickMoreInfoButton() {
        // Action
        openCDEDataFragment()
        onView(withId(R.id.info_button_cde_data_view)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "CDEDataFragmentClickMoreInfoButton")
        // Tests
        onView(withId(R.id.cde_details_title)).check(matches(isDisplayed()))
        onView(withId(R.id.cde_details_table)).check(matches(isDisplayed()))
    }

    @Test
    fun CDEDetailsFragmentClickBackButton() {
        // Action
        openCDEDetailsFragment()
        findObjectById(mDevice, "R.id.back_button_cde_details_view").waitForExists(2000)
        onView(withId(R.id.back_button_cde_details_view)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "CDEDetailsFragmentClickBackButton")
        // Tests
        onView(withId(R.id.cde_label)).check(matches(isDisplayed()))
        onView(withId(R.id.cde_table)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    fun openCDEDataFragment() {
        findObjectByDescriptor(mDevice, R.string.google_map).clickBottomRight()
        findObjectById(mDevice, "R.id.back_button_cde_data_view").waitForExists(2000)
        findObjectById(mDevice, "R.id.info_button_cde_data_view").waitForExists(2000)
    }

    fun openCDEDetailsFragment() {
        openCDEDataFragment()
        onView(withId(R.id.info_button_cde_data_view)).perform(click())
    }
}