public class BasicGLRenderer implements GLSurfaceView.Renderer {

    private final GLCameraOptions mGLCameraOptions;
    private int mBackgroundColor;
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];

    public BasicGLRenderer(GLCameraOptions glCameraOptions) {
        mGLCameraOptions = glCameraOptions;
        mBackgroundColor = Color.BLACK;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(GLColor.r(mBackgroundColor),
                GLColor.g(mBackgroundColor),
                GLColor.b(mBackgroundColor),
                GLColor.a(mBackgroundColor));

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, mGLCameraOptions.near, mGLCameraOptions.far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0,
                mGLCameraOptions.eyeX,
                mGLCameraOptions.eyeY,
                mGLCameraOptions.eyeZ,
                mGLCameraOptions.centerX,
                mGLCameraOptions.centerY,
                mGLCameraOptions.centerZ,
                mGLCameraOptions.upX,
                mGLCameraOptions.upY,
                mGLCameraOptions.upZ);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void addModel(Model3D model3D) {
        mModels.add(model3D);
    }
}