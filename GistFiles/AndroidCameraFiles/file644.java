import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

public class SceneRenderer implements CardboardView.StereoRenderer {
    private Cube cube;

    private float[] camera = new float[16];
    private float[] view = new float[16];
    private float[] mvpMatrix = new float[16];
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    public SceneRenderer() {
        this.cube = new Cube();
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        // Set the camera position
        Matrix.setLookAtM(camera, 0,
                0.0f, 0.0f, 0.0f, // eye
                0.0f, 0.0f, 0.01f, // center
                0.0f, 1.0f, 0.0f); // up
    }

    @Override
    public void onDrawEye(Eye eye) {
        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Build the ModelViewProjection Matrix
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
        Matrix.multiplyMM(mvpMatrix, 0, perspective, 0, view, 0);

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // draw cube
        cube.draw(mvpMatrix);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
        // do nothing
    }

    @Override
    public void onSurfaceChanged(int i, int i1) {
        // perspective changes are already handled by the Sdk
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        cube.initialize();
    }

    @Override
    public void onRendererShutdown() {
        // do nothing
    }

    public void trigger() {
        this.cube.randomizeColors();
    }
}
