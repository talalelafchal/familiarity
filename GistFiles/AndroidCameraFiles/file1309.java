package co.ortatech.showandhide.ui.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import co.ortatech.showandhide.R;
import co.ortatech.showandhide.application.ShowAndHideApplication;
import co.ortatech.showandhide.dagger.DaggerShowAndHideTestComponent;
import co.ortatech.showandhide.dagger.MockFileUtilitiesModule;
import co.ortatech.showandhide.dagger.MockImageProcessorModule;
import co.ortatech.showandhide.dagger.ShowAndHideTestComponent;
import co.ortatech.showandhide.model.imageprocessor.ImageProcessor;
import co.ortatech.showandhide.utilities.FileUtilities;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class HomeActivityTest {

    @Inject FileUtilities fileUtilities;
    @Inject ImageProcessor imageProcessor;

    private static class HomeActivityTestRule extends IntentsTestRule {
        private HomeActivityTestRule() {
            super(HomeActivity.class);
        }

        @Override public void beforeActivityLaunched() {
            Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
            ShowAndHideApplication app =
                    (ShowAndHideApplication) instrumentation.getTargetContext().getApplicationContext();
            ShowAndHideTestComponent component = DaggerShowAndHideTestComponent.builder()
                    .mockFileUtilitiesModule(new MockFileUtilitiesModule())
                    .mockImageProcessorModule(new MockImageProcessorModule())
                    .build();
            app.setComponent(component);
        }
    }

    @Rule
    public IntentsTestRule<HomeActivity> intentRule = new HomeActivityTestRule();

    @Before
    public void setUp() {
        // TODO(cate): This is kind of hideous.
        ShowAndHideApplication application =
                (ShowAndHideApplication) intentRule.getActivity().getApplication();
        ShowAndHideTestComponent component = (ShowAndHideTestComponent) application.component();
        component.inject(this);
    }

    @Test
    public void testLaunchActivity() {
        onView(withId(R.id.home_camera_button)).check(matches(withText("Camera")));
        onView(withId(R.id.home_gallery_button)).check(matches(withText("Gallery")));
        onView(withId(R.id.home_inspire_button)).check(matches(withText("Inspire")));
    }

    @Test
    public void testTapCameraButtonAndReturnOK() {
        // Uri needed to launch the Camera intent.
        Uri uri = Uri.parse("uri_string");
        stub(fileUtilities.getOutputMediaFileUri()).toReturn(uri);

        // Build a result to return when the activity is launched.
        Intent resultData = new Intent();
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub result for camera intent.
        intending(toPackage("com.android.camera")).respondWith(result);

        onView(withId(R.id.home_camera_button)).check(matches(withText("Camera")));
        onView(withId(R.id.home_camera_button)).perform(click());

        // Check image processor is reset.
        verify(imageProcessor).resetOriginalImage();

        // New activity should be launched
        intended(hasComponent(ImageEditingActivity.class.getName()));

        intended(toPackage("com.android.camera"));
        Intents.assertNoUnverifiedIntents();
    }

    @Test
    public void testTapCameraButtonAndCancel() {
        // Uri needed to launch the Camera intent.
        Uri uri = Uri.parse("uri_string");
        stub(fileUtilities.getOutputMediaFileUri()).toReturn(uri);

        // Build a result to return when the activity is launched.
        Intent resultData = new Intent();
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, resultData);

        // Stub result for camera intent.
        intending(toPackage("com.android.camera")).respondWith(result);

        onView(withId(R.id.home_camera_button)).check(matches(withText("Camera")));
        onView(withId(R.id.home_camera_button)).perform(click());

        // Check image processor is not reset.
        verify(imageProcessor, never()).resetOriginalImage();

        intended(toPackage("com.android.camera"));
        Intents.assertNoUnverifiedIntents();
    }

    @Test
    public void testTapGalleryButtonAndReturnOK() {
        // Stub the Uri returned by the gallery intent.
        Uri uri = Uri.parse("uri_string");

        // Build a result to return when the activity is launched.
        Intent resultData = new Intent();
        resultData.setData(uri);
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Set up result stubbing when an intent sent to "choose photo" is seen.
        intending(toPackage("com.android.gallery")).respondWith(result);

        onView(withId(R.id.home_gallery_button)).check(matches(withText("Gallery")));
        onView(withId(R.id.home_gallery_button)).perform(click());

        // Check image processor is reset.
        verify(imageProcessor).resetOriginalImage();

        // New activity should be launched
        intended(hasComponent(ImageEditingActivity.class.getName()));

        intended(toPackage("com.android.gallery"));
        Intents.assertNoUnverifiedIntents();
    }

    @Test
    public void testTapGalleryButtonAndReturnCancel() {
        // Build a result to return when the activity is launched.
        Intent resultData = new Intent();
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, resultData);

        // Set up result stubbing when an intent sent to "choose photo" is seen.
        intending(toPackage("com.android.gallery")).respondWith(result);

        onView(withId(R.id.home_gallery_button)).check(matches(withText("Gallery")));
        onView(withId(R.id.home_gallery_button)).perform(click());

        // Check image processor is not reset.
        verify(imageProcessor, never()).resetOriginalImage();

        intended(toPackage("com.android.gallery"));
        Intents.assertNoUnverifiedIntents();
    }

    @Test
    public void testTapInspireButton() {
        onView(withId(R.id.home_inspire_button)).check(matches(withText("Inspire")));
        onView(withId(R.id.home_inspire_button)).perform(click());

        // Capture web browser intent.
        intended(toPackage("com.android.browser"));

        Intents.assertNoUnverifiedIntents();
    }
}
