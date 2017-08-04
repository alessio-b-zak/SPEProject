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
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.swipeRight
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import junit.framework.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

/**
 * Created by mihajlo on 28/07/17.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class DataViewActivityTest: TestHelper() {

    private val FOLDER_NAME = "DataViewActivityTest"

    private lateinit var mDevice: UiDevice

    @Rule @JvmField
    var mActivityRule = ActivityTestRule<DataViewActivity>(DataViewActivity::class.java)

    @Before
    fun setActivity() {
        // Start the activity
        mActivityRule.activity
        // Access the device state using UiAutomator
        mDevice = UiDevice.getInstance(getInstrumentation())
    }

    @Test
    fun DataViewActivityClickGPSButton() {
        // Action
        clickButtonAndPause(R.id.gps_button)
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DataViewActivityClickGPSButton")
        // Tests
        onView(withId(R.id.layer_name)).check(matches(isDisplayed()))
    }


    @Test
    fun DataViewActivityClickMenuButton() {
        // Action
        onView(withId(R.id.data_view_hamburger_button)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DataViewActivityClickMenuButton")
        // Tests
        onView(withId(R.id.material_drawer_account_header_current)).check(matches(isDisplayed()))
        onView(withId(R.id.material_drawer_account_header_name)).check(matches(isDisplayed()))
        onView(withId(R.id.material_drawer_account_header)).check(matches(isDisplayed()))
    }

    @Test
    fun DataViewActivityClickSearchButton() {
        // Action
        onView(withId(R.id.data_view_search_button)).perform(click())
        // Object declaration
        val backButton = findObjectByDescriptor(mDevice, R.string.google_search_back_button)
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DataViewActivityClickSearchButton")
        // Tests
        assertTrue(backButton.exists())
        // Close search view because it is preventing other tests to be run
        backButton.click()
    }
}