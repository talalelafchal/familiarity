package com.example.demo.cardboard360video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.MotionEvent;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.FieldOfView;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import org.rajawali3d.cameras.Camera;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.RajawaliRenderer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * OpenGL interface of cardboard sdk. All 3d stuff happens here.
 * This class takes an android media player instance to display a
 * equirectangular video as 360° panorama.
 */
public class VideoPanoramaRenderer extends RajawaliRenderer implements CardboardView.StereoRenderer {

    // video stuff
    private MediaPlayer mediaPlayer;
    private StreamingTexture videoTexture;

    // temporary math variables
    /** position and rotation of eye camera in 3d space as matrix object */
    private Matrix4 eyeMatrix;

    /** rotation of eye camera in 3d space */
    private Quaternion eyeOrientation;

    /** position of eye camera in 3d space */
    private Vector3 eyePosition;


    /**
     * @param context      e.g. an activity
     * @param mediaPlayer  Fully initialized media player instance with loaded video.
     *                     Make sure to call play/pause by yourself.
     */
    public VideoPanoramaRenderer(Context context, MediaPlayer mediaPlayer) {
        super(context);

        this.mediaPlayer = mediaPlayer;

        // init math stuff
        eyeMatrix = new Matrix4();
        eyeOrientation = new Quaternion();
    }


    /*========================================================
     Override RajawaliRenderer abstract methods
     =========================================================*/

    @Override
    public void initScene() {
        // setup world sphere
        Sphere sphere = new Sphere(1, 24, 24);
        sphere.setPosition(0, 0, 0);

        // invert the sphere normals
        // factor "1" is two small and result in rendering glitches
        sphere.setScaleX(100);
        sphere.setScaleY(100);
        sphere.setScaleZ(-100);

        // create texture from media player video
        videoTexture = new StreamingTexture("video", mediaPlayer);

        // set material with video texture
        Material material = new Material();
        material.setColorInfluence(0f);
        try {
            material.addTexture(videoTexture);
        } catch (ATexture.TextureException e){
            throw new RuntimeException(e);
        }
        sphere.setMaterial(material);

        // add sphere to scene
        getCurrentScene().addChild(sphere);
    }

    @Override
    protected void onRender(long elapsedRealTime, double deltaTime) {
        super.onRender(elapsedRealTime, deltaTime);

        if (videoTexture != null) {
            // update texture from video content
            videoTexture.update();
        }
    }

    @Override
    public void onRenderSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        super.onRenderSurfaceDestroyed(surfaceTexture);
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void onTouchEvent(MotionEvent motionEvent) {

    }

    @Override
    public void onOffsetsChanged(float v, float v2, float v3, float v4, int i, int i2) {

    }


    /*========================================================
     Override CardboardView.StereoRenderer abstract methods
     =========================================================*/

    @Override
    public void onNewFrame(HeadTransform headTransform) {

    }

    @Override
    public void onDrawEye(Eye eye) {
        // Rajawali camera
        Camera currentCamera = getCurrentCamera();

        // cardboard field of view
        FieldOfView fov = eye.getFov();

        // update Rajawali camera from cardboard sdk
        currentCamera.updatePerspective(fov.getLeft(), fov.getRight(), fov.getBottom(), fov.getTop());
        eyeMatrix.setAll(eye.getEyeView());
        // orientation
        eyeOrientation.fromMatrix(eyeMatrix);
        currentCamera.setOrientation(eyeOrientation);
        // position
        eyePosition = eyeMatrix.getTranslation().inverse();
        currentCamera.setPosition(eyePosition);

        // render with Rajawali
        super.onRenderFrame(null);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        // tell Rajawali that cardboard sdk detected a size change
        super.onRenderSurfaceSizeChanged(null, width, height);
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        // pass opengl config to Rajawali
        super.onRenderSurfaceCreated(eglConfig, null, -1, -1);
    }

    @Override
    public void onRendererShutdown() {
        // tell Rajawali about shutdown
        super.onRenderSurfaceDestroyed(null);
    }
}
