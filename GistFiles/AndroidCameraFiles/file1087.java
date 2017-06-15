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
}