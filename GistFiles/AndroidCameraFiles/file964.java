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
  takePicture();
}
