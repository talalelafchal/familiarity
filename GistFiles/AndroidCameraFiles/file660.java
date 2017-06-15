public class SimpleCameraPreview extends SurfaceView implements SurfaceHolder.Callback {

 private static final String TAG = "Simple Camera Preview";
 private final SurfaceHolder mHolder;
 private final Camera        mCamera;
  
 @SuppressWarnings("deprecation")
 public SimpleCameraPreview(Context context, Camera camera) {
     super(context);
     mCamera = camera;
     // Install a SurfaceHolder.Callback so we get notified when the
     // underlying surface is created and destroyed.
     mHolder = getHolder();
     mHolder.addCallback(this);

     // deprecated setting, but required on Android versions prior to 3.0
     mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
 }

 @Override
 public void surfaceCreated(SurfaceHolder holder) {
     // empty. Taken care of in surfaceChanged.
 }

 @Override
 public void surfaceDestroyed(SurfaceHolder holder) {
     // empty. Take care of releasing the Camera preview in the activity.
     Log.d(TAG, "Surface was Destroyed");
 }

 @Override
 public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
     // If your preview can change or rotate, take care of those events here.
     // Make sure to stop the preview before resizing or reformatting it.
     if (mHolder.getSurface() == null) {
         // preview surface does not exist
         Log.d(TAG, "Preview Surface does not exist");
         return;
     }

     // stop preview before making changes
     try {
         mCamera.stopPreview();
         Log.d(TAG, "Camera Stopped Successfully");
     } catch (Exception e) {
         Log.d(TAG, "Error Stopping Camera, it most likely is a non-existent preview");
     }

     // set preview size and make any resize, rotate or
     // reformatting changes here
     // start preview with new settings

     try {
         mCamera.setPreviewDisplay(mHolder);
         mCamera.startPreview();
         Log.d(TAG, "Preview Started Successfully");
     } catch (Exception e) {
         Log.d(TAG, "Error starting camera preview: " + e.getMessage());
     }
 }
 }