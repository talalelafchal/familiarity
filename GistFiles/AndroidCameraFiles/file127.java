package com.feigdev.gg_test;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import com.feigdev.reusableandroidutils.graphics.PhotoCallback;
import com.feigdev.reusableandroidutils.graphics.PhotoHandler;
import com.google.android.glass.sample.camera.CameraPreview;

import java.io.IOException;

/**
 * Created by ejf3 on 3/11/14.
 */
public class ImageGrabFrag extends Fragment implements PhotoCallback {
    private static final String TAG = "ImageGrabFrag";
    private Camera camera;
    private CameraPreview cameraPreview;
    private SurfaceView preview;
    private SurfaceHolder holder;
    private int count = 0;
    private static boolean isAlive = false;
    private GifFlowControl flowControl;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        flowControl = (GifFlowControl)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main2, container, false);

        preview = (SurfaceView) rootView.findViewById(R.id.preview1);
        preview.getHolder().addCallback(mSurfaceHolderCallback);

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        isAlive = true;
        count = 0;

        new GlassPhotoDelay().execute();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        isAlive = false;

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        super.onPause();
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
    }

    private void takePicture() {
        Log.d(TAG, "takePicture");
        count++;

        if (!isAlive)
            return;

        camera.takePicture(null, null,
                new PhotoHandler(this));
    }

    @Override
    public void pictureTaken(String filename){
        GGMainActivity.listOfFiles.add(filename);
        if (count >= 5){
            Log.d(TAG, "taken 5");
            flowControl.startBuild();
            return;
        }
        if (!isAlive)
            return;

        camera.startPreview();
        takePicture();
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


}
