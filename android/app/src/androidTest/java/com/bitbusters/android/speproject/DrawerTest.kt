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
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.swipeLeft
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*

import org.hamcrest.core.IsNot.not
/**
 * Created by mihajlo on 28/07/17.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class DrawerTest: TestHelper() {
    private val TAG = "DRAWER_TEST"

    private val FOLDER_NAME = "DrawerTest"

    private lateinit var mDevice: UiDevice

    @Rule @JvmField
    val mActivityRule = ActivityTestRule<DataViewActivity>(DataViewActivity::class.java)

    @Before
    fun setActivity() {
        // Start the activity
        mActivityRule.activity
        // Access the device state using UiAutomator
        mDevice = UiDevice.getInstance(getInstrumentation())
        // Open drawer
        onView(withId(R.id.data_view_hamburger_button)).perform(click())
    }

    @Test
    fun DrawerOpenPermitLayer() {
        // Object declaration
        val permitItem = findObjectByText(mDevice, R.string.drawer_permit)
        // Action
        permitItem.click()
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DrawerOpenPermitLayer")
        // Tests
        onView(withId(R.id.layer_name)).check(matches(withText(R.string.drawer_permit)))
        onView(withId(R.id.material_drawer_account_header)).check(matches(not(isDisplayed())))
    }

    @Test
    fun DrawerOpenWIMSLayer() {
        // Object declaration
        val wimsItem = findObjectByText(mDevice, R.string.drawer_wims)
        // Action
        wimsItem.click()
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DrawerOpenWIMSLayer")
        // Tests
        onView(withId(R.id.layer_name)).check(matches(withText(R.string.drawer_wims)))
        onView(withId(R.id.material_drawer_account_header)).check(matches(not(isDisplayed())))
    }

    @Test
    fun DrawerOpenCDELayer() {
        // Object declaration
        val cdeItem = findObjectByText(mDevice, R.string.drawer_cde)
        // Action
        cdeItem.click()
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DrawerOpenCDELayer")
        // Tests
        onView(withId(R.id.layer_name)).check(matches(withText(R.string.drawer_cde)))
        onView(withId(R.id.material_drawer_account_header)).check(matches(not(isDisplayed())))
    }

    @Test
    fun DrawerOpenInfoFragment() {
        // Object declaration
        val myAreaItem = findObjectByText(mDevice, R.string.drawer_info)
        // Action
        myAreaItem.click()
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DrawerOpenInfoFragment")
        // Tests
        onView(withId(R.id.info_title)).check(matches(isDisplayed()))
        onView(withId(R.id.info_description)).check(matches(isDisplayed()))
        onView(withId(R.id.material_drawer_account_header)).check(matches(not(isDisplayed())))
    }

    @Test
    fun DrawerOpenMyAreaFragmentLoadingScreen() {
        // Object declaration
        val myAreaItem = findObjectByText(mDevice, R.string.drawer_my_area)
        // Action
        myAreaItem.click()
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DrawerOpenMyAreaFragmentLoadingScreen")
        // Tests
        onView(withId(R.id.material_drawer_account_header)).check(matches(not(isDisplayed())))
    }

    @Test
    fun DrawerOpenMyAreaFragmentWaitToOpen() {
        // Object declaration
        val permitItem = findObjectByText(mDevice, R.string.drawer_my_area)
        // Action
        permitItem.click()
        SystemClock.sleep(3000)
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DrawerOpenMyAreaFragmentWaitToOpen")
        // Tests
        onView(withId(R.id.my_area_wims_button)).check(matches(isDisplayed()))
        onView(withId(R.id.my_area_permit_button)).check(matches(isDisplayed()))
        onView(withId(R.id.my_area_title)).check(matches(isDisplayed()))
        onView(withId(R.id.my_area_summary_table)).check(matches(isDisplayed()))
        onView(withId(R.id.material_drawer_account_header)).check(matches(not(isDisplayed())))
    }

    @Test
    fun DrawerClickLogo() {
        // Action
        onView(withId(R.id.material_drawer_account_header_current)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DrawerClickLogo")
        // Tests
        onView(withId(R.id.layer_name)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.material_drawer_account_header)).check(matches(not(isDisplayed())))
    }

    @Test
    fun DrawerCloseByClick() {
        // Action
        onView(withId(R.id.data_view_search_button)).perform(click())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DrawerCloseByClick")
        // Tests
        onView(withId(R.id.layer_name)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.material_drawer_account_header)).check(matches(not(isDisplayed())))
    }

    @Test
    fun DrawerCloseBySwipe() {
        // Action
        onView(withId(R.id.material_drawer_account_header)).perform(swipeLeft())
        // Screenshot
        takeScreenshot(mDevice, FOLDER_NAME, "DrawerCloseBySwipe")
        // Tests
        onView(withId(R.id.layer_name)).check(matches(isDisplayed()))
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.material_drawer_account_header)).check(matches(not(isDisplayed())))
    }

}