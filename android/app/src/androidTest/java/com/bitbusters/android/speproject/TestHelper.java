package com.bitbusters.android.speproject;

import android.os.Environment;
import android.os.SystemClock;
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

    private static final String TAG = "TEST_HELPER";

    public static final String SAMPLING_POINT_MARKER = "Sample_Point.";
    public static final String PICTURE_MARKER = "Picture_Point.";

    public static final String SHUTTER_BUTTON = "com.android.gallery3d:id/shutter_button";
    public static final String CONFIRM_PICTURE_BUTTON = "com.android.gallery3d:id/btn_done";
    public static final String CANCEL_PICTURE_BUTTON = "com.android.gallery3d:id/btn_cancel";

    public static final int GPS_BUTTON = R.id.gps_button;
    public static final int SAMPLING_POINT_BUTTON = 1;

    public static final String SUBMIT_FORM_BUTTON = "android:id/button1";
    public static final String CANCEL_FORM_BUTTON = "android:id/button2";

    private static final String SCREENSHOT_PATH = Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        + "/Screenshots/";

    // Useful when waiting for transition
    public void clickButtonAndPause(final int button) {
        onView(withId(button)).perform(click());
        SystemClock.sleep(1000);
    }

    public void clickMarker(UiDevice device, String marker) {
        UiObject markerObject = device.findObject(new UiSelector().descriptionContains(marker));
        try {
            // Photo marker is usually on top of the location marker and in order to avoid clicking
            // the location marker we perform a clickTopLeft on the Picture Marker.
            if(marker.equals(PICTURE_MARKER)) {
                markerObject.clickTopLeft();
            } else {
                markerObject.click();
            }
            Log.i(TAG, marker + " Clicked");
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, marker + " NOT Found");
        }
    }

    public void clickUiObject(UiDevice device, String button) {
        UiObject cancelFormButton = device.findObject(new UiSelector().resourceId(button));
        try {
            cancelFormButton.click();
            Log.i(TAG, button + " Clicked");
        } catch(UiObjectNotFoundException e) {
            Log.e(TAG, button + " NOT Found");
        }
        // Sometimes camera just focuses on click. Therefore we have a special case for shutter button.
        // Until the photo is taken (i.e. confirm button is visible) keep clicking the shutter button.
        if(button.equals(SHUTTER_BUTTON)) {
            UiObject confirmPictureButton = device.findObject(new UiSelector().resourceId(CONFIRM_PICTURE_BUTTON));
            if(!confirmPictureButton.exists()) clickUiObject(device, SHUTTER_BUTTON);
        }
    }

    public void takeScreenshot(UiDevice device, String folderName, String name) {
        SystemClock.sleep(1000);
        File file = new File(SCREENSHOT_PATH + folderName + "/" + name + ".png");
        device.takeScreenshot(file);
    }
}
