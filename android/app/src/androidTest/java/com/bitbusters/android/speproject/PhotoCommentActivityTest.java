package com.bitbusters.android.speproject;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by mihajlo on 12/06/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PhotoCommentActivityTest extends TestHelper{

    private static final String TAG = "PHOTO_SUBMISSION_TEST";

    private static final String FOLDER_NAME = "PhotoCommentActivityTest";

    private static final String TEST_COMMENT = "Test Comment";

    private UiDevice mDevice;

    @Rule
    public ActivityTestRule<DataViewActivity> mActivityRule = new ActivityTestRule<>(DataViewActivity.class);

    @Before
    public void setActivity() {
        // Start the activity
        mActivityRule.getActivity();
        // Access the device state using UiAutomator
        mDevice = UiDevice.getInstance(getInstrumentation());
    }

    @Test
    public void PhotoCommentActivitySuccessfullyTakePicture() {
        shootAndConfirmPicture();
        takeScreenshot(mDevice, FOLDER_NAME, "PhotoCommentActivitySuccessfullyTakePicture");
        onView(withId(R.id.editText)).check(matches(isDisplayed()));
        onView(withId(R.id.submit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button_photo_view)).check(matches(isDisplayed()));
    }

    @Test
    public void PhotoCommentActivityUnsuccessfullyTakePicture() {
        shootAndCancelPicture();
        takeScreenshot(mDevice, FOLDER_NAME, "PhotoCommentActivityUnsuccessfullyTakePicture");
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.gps_button)).check(matches(isDisplayed()));
        onView(withId(R.id.info_button)).check(matches(isDisplayed()));
    }

    @Test
    public void PhotoCommentActivityClickBackButton() {
        shootAndConfirmPicture();
        onView(withId(R.id.back_button_photo_view)).perform(click());
        takeScreenshot(mDevice, FOLDER_NAME, "PhotoCommentActivityClickBackButton");
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.cam_button)).check(matches(isDisplayed()));
        onView(withId(R.id.sp_view_button)).check(matches(isDisplayed()));
    }

    @Test
    public void PhotoCommentActivityCheckRightTagDisplayed() {
        shootAndConfirmPicture();
        enterCommentAndTag();
        takeScreenshot(mDevice, FOLDER_NAME, "PhotoCommentActivityCheckRightTagDisplayed");
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(is("Overshading"))));
    }

    @Test
    public void PhotoCommentActivityCancelSubmission() {
        shootAndConfirmPicture();
        enterCommentAndTag();
        onView(withId(R.id.submit_button)).perform(click());
        clickUiObject(mDevice, CANCEL_FORM_BUTTON);
        takeScreenshot(mDevice, FOLDER_NAME, "PhotoCommentActivityCancelSubmission");
        onView(withId(R.id.editText)).check(matches(isDisplayed()));
        onView(withId(R.id.submit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));
    }

    @Test
    public void PhotoCommentActivityConfirmSubmission() {
        shootAndConfirmPicture();
        enterCommentAndTag();
        onView(withId(R.id.submit_button)).perform(click());
        clickUiObject(mDevice, SUBMIT_FORM_BUTTON);
        takeScreenshot(mDevice, FOLDER_NAME, "PhotoCommentActivityConfirmSubmission");
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.cam_button)).check(matches(isDisplayed()));
    }

    public void enterCommentAndTag() {
        onView(withId(R.id.editText)).perform(typeText(TEST_COMMENT), closeSoftKeyboard());
        onView(withId(R.id.spinner)).perform(click());
        onData(hasToString(is("Overshading"))).perform(click());
    }

    public void shootAndConfirmPicture() {
        onView(withId(R.id.cam_button)).perform(click());
        clickUiObject(mDevice, SHUTTER_BUTTON);
        clickUiObject(mDevice, CONFIRM_PICTURE_BUTTON);
    }

    public void shootAndCancelPicture() {
        onView(withId(R.id.cam_button)).perform(click());
        clickUiObject(mDevice, SHUTTER_BUTTON);
        clickUiObject(mDevice, CANCEL_PICTURE_BUTTON);
    }

}
