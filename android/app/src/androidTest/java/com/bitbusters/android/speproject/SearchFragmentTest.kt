package com.bitbusters.android.speproject

import android.os.SystemClock
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

import junit.framework.Assert.assertTrue

/**
 * Created by mihajlo on 28/07/17.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class SearchFragmentTest: TestHelper() {

    private val FOLDER_NAME = "SearchFragmentTest"

    private val BRISTOL = "Bristol A"

    private lateinit var mDevice: UiDevice

    @Rule @JvmField
    val mActivityRule = ActivityTestRule<DataViewActivity>(DataViewActivity::class.java)

    @Before
    fun setActivity() {
        // Start the activity
        mActivityRule.activity
        // Access the device state using UiAutomator
        mDevice = UiDevice.getInstance(getInstrumentation())
        // Open search fragment
        onView(withId(R.id.data_view_search_button)).perform(click())
    }

    @Test
    fun SearchFragmentClickBackButton() {
        // Object declaration
        val backButton = findObjectByDescriptor(mDevice, R.string.google_search_back_button)
        // Action
        backButton.click()
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "SearchFragmentClickBackButton")
        // Tests
        onView(withId(R.id.data_view_hamburger_button)).check(matches(isDisplayed()))
        onView(withId(R.id.data_view_search_button)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun SearchFragmentEnterText() {
        // Object declaration
        val backButton = findObjectByDescriptor(mDevice, R.string.google_search_back_button)
        // Action
        searchFor(BRISTOL)
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "SearchFragmentEnterText")
        // Tests
        assertTrue(findObjectByText(mDevice, R.string.bristol_airport).exists())
        assertTrue(findObjectByDescriptor(mDevice, R.string.google_search_cancel_button).exists())
        // Close search view because it is preventing other tests to be run
        backButton.click()
    }

    @Test
    fun SearchFragmentPerformSearch() {
        // Action
        searchFor(BRISTOL)
        findObjectByText(mDevice, R.string.bristol_airport).click()
        // Screenshot
        SystemClock.sleep(1000)
        takeScreenshot(mDevice, FOLDER_NAME, "SearchFragmentPerformSearch")
        // Tests
        onView(withId(R.id.data_view_hamburger_button)).check(matches(isDisplayed()))
        onView(withId(R.id.data_view_search_button)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    fun searchFor(input: String) {
        val inputField = findObjectByText(mDevice, R.string.google_search_input_field_holder)
        inputField.text = input
    }
}