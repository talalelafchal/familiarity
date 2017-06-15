public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Size previewSize;

    private int containerWidth, containerHeight;

    public CameraPreview(Context context, Camera camera, int containerWidth, int containerHeight) {
        super(context);
        mCamera = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        this.containerWidth = containerWidth;
        this.containerHeight = containerHeight;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);

            previewSize = ImageUtils.getOptimalPreviewSize(mCamera, containerWidth, containerHeight);

            Camera.Parameters parameters = mCamera.getParameters();
            
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            parameters.setExposureCompensation(0);

            mCamera.setParameters(parameters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e){}

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Activity is handling this
    }

}