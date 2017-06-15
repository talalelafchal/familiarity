package com.feigdev.gg_test;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
// requires library project https://github.com/emil10001/ReusableAndroidUtils
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
    private static boolean isAlive = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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

        if (!isAlive)
            return;

        camera.takePicture(null, null,
                new PhotoHandler(this));
    }

    @Override
    public void pictureTaken(String filename){
        Log.d(TAG, "captured file " + filename);
        getActivity().finish();
    }

    private final SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder hldr) {
            holder = hldr;
            initCamera();
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
