package com.dailycation.hoffer.util;

import android.hardware.Camera;
import android.util.Log;

/**
 * @author hehu
 * @version 1.0 2016/6/30
 */
public class CameraUtil {
    private static final String LOG_TAG = CameraUtil.class.getSimpleName();
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            if(cameraId == -1)
                c = Camera.open();
            else
                c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.d(LOG_TAG, "Camera " + cameraId + " is not available: " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }
    
    /**
     * check camera
     */
    public boolean isCameraOk(){
        Camera mCamera = getCameraInstance(0);
        Camera.CameraInfo cameraInfo = null;
        if(mCamera!=null) {
            cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(0, cameraInfo);
        }
        if (mCamera != null && cameraInfo != null) {
            // Camera is not available, display error message
            mCamera.release();
            return true;
        }else return false;
    }
}
