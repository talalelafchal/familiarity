//CameraPreview.java
private class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	    private SurfaceHolder mHolder;
	    private Camera mCamera;

	    public CameraPreview(Context context, Camera camera) {
	        super(context);
	        mCamera = camera;

	        Log.v(TAG,"In CameraPreview");

	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        mHolder = getHolder();

	        Log.v(TAG,"Got holder");

	        mHolder.addCallback(this);

	        Log.v(TAG,"Added callback");

	        // deprecated setting, but required on Android versions prior to 3.0
	        //mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    }

	    public void surfaceCreated(SurfaceHolder holder) {
	        // The Surface has been created, now tell the camera where to draw the preview.
	        try {
	            Log.v(TAG,"in surface created");
	            mCamera.setPreviewDisplay(holder);
	            Log.v(TAG,"set preview display");
	            mCamera.startPreview();
	            Log.v(TAG,"preview started");
	        } catch (IOException e) {
	            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
	        }
	    }

	    public void surfaceDestroyed(SurfaceHolder holder) {
	        // empty. Take care of releasing the Camera preview in your activity.
	        mCamera.stopPreview();
	        mCamera.release();
	        mCamera = null;
	    }

	    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	        // If your preview can change or rotate, take care of those events here.
	        // Make sure to stop the preview before resizing or reformatting it.

	        Log.v(TAG,"in surface changted");

	        if (mHolder.getSurface() == null){
	          // preview surface does not exist
	            Log.v(TAG,"surface don't exist");
	          return;
	        }

	        // stop preview before making changes
	        try {
	            mCamera.stopPreview();
	            Log.v(TAG,"stopped preview");
	        } catch (Exception e){
	          // ignore: tried to stop a non-existent preview
	            Log.v(TAG,"preview e");
	        }

	        // start preview with new settings
	        try {
	            Log.v(TAG,"startpreview");
	            mCamera.setPreviewDisplay(mHolder);
	            mCamera.startPreview();

	        } catch (Exception e){
	            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
	        }
	    }
	}