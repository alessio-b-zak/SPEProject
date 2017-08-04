package com.bitbusters.android.speproject

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId

import org.hamcrest.core.IsNot.not

/**
 * Created by mihajlo on 28/07/17.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class DischargePermitLayerTest: TestHelper() {

    private val FOLDER_NAME = "DischargePermitLayerTest"

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
        // Open discharge permit fragment
        onView(withId(R.id.data_view_hamburger_button)).perform(click())
        findObjectByText(mDevice, R.string.drawer_permit).click()
    }

    @Test
    fun DischargePermitLayerClickPermitMarker() {
        // Action
        findObjectByDescriptor(mDevice, R.string.marker_permit).click()
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "PermitLayerClickPermitMarker")
        // Tests
        onView(withId(R.id.permit_holder_name)).check(matches(isDisplayed()))
        onView(withId(R.id.permit_table)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun DischargePermitDataFragmentClickBackButton() {
        // Action
        openDischargePermitDataFragment()
        onView(withId(R.id.back_button_permit_data_view)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DischargePermitDataFragmentClickBackButton")
        // Tests
        onView(withId(R.id.data_view_hamburger_button)).check(matches(isDisplayed()))
        onView(withId(R.id.data_view_search_button)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    fun openDischargePermitDataFragment() {
        findObjectByDescriptor(mDevice, R.string.marker_permit).click()
    }

}