package com.epimorphics.android.myrivers

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
import android.support.test.espresso.matcher.ViewMatchers.*

import junit.framework.Assert.assertTrue

/**
 * Created by mihajlo on 28/07/17.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class MyAreaFragmentTest: TestHelper() {

    private val FOLDER_NAME = "MyAreaFragmentTest"

    private lateinit var mDevice: UiDevice

    @Rule @JvmField
    val mActivityRule = ActivityTestRule<DataViewActivity>(DataViewActivity::class.java)

    @Before
    fun setActivity() {
        // Start the activity
        mActivityRule.activity
        // Access the device state using UiAutomator
        mDevice = UiDevice.getInstance(getInstrumentation())
        // Open MyAreaFragment
        onView(withId(R.id.data_view_hamburger_button)).perform(click())
        findObjectByText(mDevice, R.string.drawer_my_area).click()
        SystemClock.sleep(3000)
    }

    @Test
    fun MyAreaFragmentClickNearestWimsButton() {
        // Action
        onView(withId(R.id.my_area_wims_button)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "MyAreaFragmentClickNearestWimsButton")
        // Tests
        onView(withId(R.id.layer_name)).check(matches(withText(R.string.drawer_wims)))
        assertTrue(findObjectByDescriptor(mDevice, R.string.marker_wims).exists())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun MyAreaFragmentClickNearestPermitButton() {
        // Action
        onView(withId(R.id.my_area_permit_button)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "MyAreaFragmentClickNearestPermitButton")
        // Tests
        onView(withId(R.id.layer_name)).check(matches(withText(R.string.drawer_permit)))
        assertTrue(findObjectByDescriptor(mDevice, R.string.marker_permit).exists())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

    @Test
    fun MyAreaFragmentClickBackButton() {
        // Action
        onView(withId(R.id.back_button_my_area_view)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "MyAreaFragmentClickBackButton")
        // Tests
        onView(withId(R.id.data_view_hamburger_button)).check(matches(isDisplayed()))
        onView(withId(R.id.data_view_search_button)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }

}