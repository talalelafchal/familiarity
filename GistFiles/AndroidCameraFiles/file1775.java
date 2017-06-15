/*
 * Copyright 2013 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.grafika;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * More or less straight out of TextureView's doc.
 * <p>
 * TODO: add options for different display sizes, frame rates, camera selection, etc.
 */
public class LiveCameraActivity extends Activity implements TextureView.SurfaceTextureListener {
    private static final String TAG = MainActivity.TAG;

    private Camera mCamera;
    private TextureView mTextureView;
    private ViewGroup.LayoutParams mLayoutParams;
    private int mCameraId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);

        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);

        frameLayout.addView(mTextureView);

        addButton(frameLayout);

        setContentView(frameLayout);

        mLayoutParams = mTextureView.getLayoutParams();
    }

    private void addButton(FrameLayout frameLayout) {
        Button button = new Button(this);
        button.setText("switch camera");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraId = (mCameraId + 1) % 2;
                loadCamera(mTextureView.getSurfaceTexture());
            }
        });

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;

        frameLayout.addView(button, lp);
    }

    private void loadCamera(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }

        int cameraId = mCameraId;
        mCamera = Camera.open(cameraId);
        int orientation = getCameraDisplayOrientation(LiveCameraActivity.this, cameraId);
        if (mCamera == null) {
            // Seeing this on Nexus 7 2012 -- I guess it wants a rear-facing camera, but
            // there isn't one.  TODO: fix
            throw new RuntimeException("Default camera not available");
        }

        int desiredWidth = 640;
        int desiredHeight = 480;

        Camera.Parameters params = mCamera.getParameters();
        CameraUtils.choosePreviewSize(params, desiredWidth, desiredHeight);
        mCamera.setDisplayOrientation(orientation);

        if (orientation % 180 != 0) {
            mLayoutParams.width = params.getPreviewSize().height;
            mLayoutParams.height = params.getPreviewSize().width;
        } else {
            mLayoutParams.width = params.getPreviewSize().width;
            mLayoutParams.height = params.getPreviewSize().height;
        }
        mTextureView.setLayoutParams(mLayoutParams);

        try {
            mCamera.setParameters(params);
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        loadCamera(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
        //Log.d(TAG, "updated, ts=" + surface.getTimestamp());
    }

    public static int getCameraDisplayOrientation(
            Activity activity,
            int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

}
