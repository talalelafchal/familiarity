package net.dhruvpatel.examplefrontcamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by Dhruv on 13-01-2016.
 */

public class CameraManager {
    private Camera mCamera;
    private Camera.PictureCallback mPicture;
    private static Context mContext;
    private boolean cameraFront = false;
    private CameraPreview mPreview;

    public CameraManager(Context ctx){
        mContext = ctx;
    }

    public static int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
    public boolean hasCamera() {
        //check if the device has camera
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public static int getCameraOrientation(){

        //Camera.Parameters parameters = mCamera.getParameters();

        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(CameraManager.findFrontFacingCamera(), camInfo);


        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
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

        int result;
        result = (camInfo.orientation + degrees) % 360;
        return (360 - result) % 360;
    }
}
