package com.epimorphics.android.myrivers

import android.content.Intent
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.Until

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.InstrumentationRegistry.getContext
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId


/**
 * Created by mihajlo on 28/07/17.
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class SplashActivityTest: TestHelper() {

    private val FOLDER_NAME = "SplashActivityTest"

    private val PACKAGE = "com.epimorphics.android.myrivers"

    private val LAUNCH_TIMEOUT = 5000

    private lateinit var mDevice: UiDevice

    @Rule @JvmField
    var mActivityRule = ActivityTestRule(DataViewActivity::class.java)

    @Before
    fun setActivity() {
        // Start the activity
        mActivityRule.activity
        // Access the device state using UiAutomator
        mDevice = UiDevice.getInstance(getInstrumentation())
        // Start from the home screen
        mDevice.pressHome()
    }

    @Test
    fun SplashActivityTestLaunchApp() {
        // Wait for launcher
        val launcherPackage = mDevice.launcherPackageName
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT.toLong())

        // Launch the app
        val context = getContext()
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE)

        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(PACKAGE).depth(0)), LAUNCH_TIMEOUT.toLong())

        takeScreenshot(mDevice, FOLDER_NAME, "SplashActivityTestLaunchApp")

        onView(withId(R.id.map)).check(matches(isDisplayed()))
        onView(withId(R.id.gps_button)).check(matches(isDisplayed()))
    }
}