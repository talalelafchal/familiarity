package com.example.mootoh.camera1sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {
    private Camera camera;
    private Camera.CameraInfo cameraInfo;

    // return -1 unless available
    private int getBackCameraId(Camera.CameraInfo cameraInfo) {
        int id = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                id = i;
                break;
            }
        }
        return id;
    }

    private void setupCamera(SurfaceHolder surfaceHolder) {
        cameraInfo = new Camera.CameraInfo();
        camera = Camera.open(getBackCameraId(cameraInfo));

        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        camera.setDisplayOrientation(getDisplayOrientation());
    }

    private SurfaceHolder.Callback mSurfaceListener = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            setupCamera(holder);
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            camera.release();
            camera = null;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            camera.startPreview();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView mySurfaceView = (SurfaceView)findViewById(R.id.surface_view);
        SurfaceHolder holder = mySurfaceView.getHolder();
        holder.addCallback(mSurfaceListener);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Button btn = (Button) findViewById(R.id.shutter_button);
        btn.setOnClickListener(new View.OnClickListener() {
            int degree = -1;

            @Override
            public void onClick(View v) {
                camera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        degree = getDisplayOrientation();
                    }
                }, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bitmap = ImageTools.toBitmap(data);
                        bitmap = ImageTools.rotate(bitmap, degree);

                        FileOutputStream output = null;
                        try {
                            File file = new File(MainActivity.this.getExternalFilesDir(null), "pic.jpg");
                            output = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    static class ImageTools {
        public static Bitmap toBitmap(byte[] data) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        public static Bitmap rotate(Bitmap in, int angle) {
            Matrix mat = new Matrix();
            mat.postRotate(angle);
            return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), mat, true);
        }
    }

    int getDisplayRotation() {
        int degrees = 0;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        return degrees;
    }

    public int getDisplayOrientation() {
        int degrees = getDisplayRotation();

        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }

        return result;
    }
}
