package com.bitbusters.android.speproject;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by mihajlo on 12/06/17.
 */


public class TestHelper {

    private static final String SCREENSHOT_PATH = Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        + "/Screenshots/";

    // Useful when waiting for transition
    public void clickButtonAndPause(final int button) {
        onView(withId(button)).perform(click());
        SystemClock.sleep(1000);
    }

    public UiObject findObjectByDescriptor(UiDevice device, int descriptorId) {
        String descriptor = getResourceString(descriptorId);
        return device.findObject(new UiSelector().descriptionContains(descriptor));
    }

    public UiObject findObjectByText(UiDevice device, int textId) {
        String text = getResourceString(textId);
        return device.findObject(new UiSelector().textContains(text));
    }

    public void takeScreenshot(UiDevice device, String folderName, String name) {
        SystemClock.sleep(1000);
        File folder = new File(SCREENSHOT_PATH + folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(SCREENSHOT_PATH + folderName + "/" + name + ".png");
        device.takeScreenshot(file);
    }

    private String getResourceString(int id) {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        return targetContext.getResources().getString(id);
    }
}
