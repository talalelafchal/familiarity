public class CameraSurface extends SurfaceView implements Callback {
  private Camera mCamera;
  private SurfaceHolder mHolder;
  private Context c;
  private static final String CNAME = ScanActivity.class.getName();
  private PreviewCallback cb;

  public CameraPreview(Context context, PreviewCallback cb) {
    super(context);
    this.c = context;
    mHolder = getHolder();
    mHolder.addCallback(this);
    this.cb = cb;
  }

  private Camera getCameraInstance() {
    Log.i(CNAME, "Getting camera instance");
    Camera c = null;

    if (!this.c.getPackageManager().hasSystemFeature(
        PackageManager.FEATURE_CAMERA)) {
      Log.i(CNAME, "No camera exists");
      displayCameraFailure();
      return null;
    }

    try {
      c = Camera.open();
      // c.autoFocus(autoFocusCB);
      c.setPreviewCallback(cb);
    } catch (RuntimeException e) {
      Log.e(CNAME, e.getMessage());
      displayCameraFailure();
    }
    return c;
  }

  private void displayCameraFailure() {
    Log.e(CNAME, "The camera could not be accessed");
    AlertDialog.Builder builder = new Builder(c);
    builder.setMessage("Could not access camera").setTitle("Error");
    builder.setIcon(android.R.drawable.ic_dialog_alert);
    builder.setPositiveButton("Okay",
        new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            // Intent i = new
            // CameraPreview(this,MainActivity.class);
          }

        });
    builder.show();
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width,
      int height) {
    Log.i(CNAME, "Surface changed");

    if (mHolder.getSurface() == null)
      return;
    if (mCamera != null) {
      try {
        mCamera.stopPreview();
        mCamera.setPreviewCallback(cb);
        mCamera.setPreviewDisplay(mHolder);

        Camera.Parameters p = mCamera.getParameters();
        
        if (getRotation() == Surface.ROTATION_0) {
          p.setPreviewSize(height, width);
          mCamera.setDisplayOrientation(90);
          
        } else if (getRotation() == Surface.ROTATION_270) {
          p.setPreviewSize(width,height);
          mCamera.setDisplayOrientation(180);
        }
        if (p.getSupportedFocusModes().contains(
            Parameters.FOCUS_MODE_AUTO)) {
          p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        mCamera.setParameters(p);
        mCamera.startPreview();
      } catch (Exception e) {
        Log.e("CameraPreview", e.getMessage());
      }
    }
    Log.i(CNAME, "Surface changed end");
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    Log.i(CNAME, "Surface created");
    if (mHolder.getSurface() == null) {
      return;
    }
    if (mCamera == null) {
      mCamera = getCameraInstance();
      mCamera.setPreviewCallback(cb);
    }
    try {
      mCamera.stopPreview();
      mCamera.setPreviewDisplay(mHolder);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Log.i(CNAME, "Surface created end");
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    Log.i(CNAME, "Surface destroyed");
    closeCamera();
  }

  private void closeCamera() {
    Log.i(CNAME, "Closing camera");
    if (mCamera != null) {
      mCamera.stopPreview();
      mCamera.setPreviewCallback(null);
      mCamera.release();
      mCamera = null;
    }
  }
}