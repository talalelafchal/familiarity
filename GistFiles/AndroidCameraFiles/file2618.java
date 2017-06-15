public void onResume() {
  Log.d(TAG, "onResume");
  super.onResume();
  isAlive = true;

  new GlassPhotoDelay().execute();
}

private void initCamera() {
  Log.d(TAG, "initCamera");

  // do we have a camera?
  if (!getActivity().getPackageManager()
      .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
    Log.d(TAG, "No camera on this device");
    return;
  }

  camera = Camera.open();
  camera.setDisplayOrientation(0);

  /**
   * The camera preview on Glass needs certain special parameters to run properly
   * SO help: http://stackoverflow.com/a/19257078/974800
   */
  Camera.Parameters params = camera.getParameters();
  params.setPreviewFpsRange(30000, 30000);
  params.setJpegQuality(90);
  // hard-coding is bad, but I'm a bit lazy
  params.setPictureSize(640, 480);
  params.setPreviewSize(640, 480);
  camera.setParameters(params);

  cameraPreview = new CameraPreview(getActivity());
  cameraPreview.setCamera(camera);
  try {
    camera.setPreviewDisplay(holder);
  } catch (IOException e) {
    e.printStackTrace();
  }
  camera.startPreview();
  // note we removed the call to takePicture()
}

/**
 * There is currently a race condition where using a voice command to launch,
 * then trying to grab the camera will fail, because the microphone is still locked
 * <p/>
 * http://stackoverflow.com/a/20154537/974800
 * https://code.google.com/p/google-glass-api/issues/detail?id=259
 */
private class GlassPhotoDelay extends AsyncTask<Void, Void, Void> {

  @Override
  protected Void doInBackground(Void... params) {
    Log.d(TAG, "GlassPhotoDelay");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void params) {
    if (!isAlive)
      return;

    initCamera();
    takePicture();
  }
}

private final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

  @Override
  public void surfaceCreated(SurfaceHolder hldr) {
    holder = hldr;
    // removed initCamera call
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    // Nothing to do here.
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    // Nothing to do here.
  }
};
