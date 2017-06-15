// Copyright (c) 2016 boyw165
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
//    The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
//    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.whatever.util;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

public class CameraUtils {

    private static final String TAG = "CameraUtils";

    private static int frontCameraId = -1;
    private static int backCameraId = -1;
    private static int cameraNumber = -1;

    public static int getCameraNumber() {
        if (cameraNumber == -1) {
            cameraNumber = Camera.getNumberOfCameras();
        }
        return cameraNumber;
    }

    public static int getFrontCameraId() {
        if (frontCameraId == -1) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < getCameraNumber(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    frontCameraId = i;
                    break;
                }
            }
        }
        return frontCameraId;
    }

    public static Camera.CameraInfo getCameraInfo(int id) {
        if (id >= 0 && id < getCameraNumber()) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(id, cameraInfo);
            return cameraInfo;
        }
        return null;
    }

    public boolean hasFrontCamera() {
        return getFrontCameraId() != -1;
    }

    public boolean hasBackCamera() {
        return getBackCameraId() != -1;
    }

    public static int getBackCameraId() {
        if (backCameraId == -1) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < getCameraNumber(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    backCameraId = i;
                    break;
                }
            }
        }
        return backCameraId;
    }

    public static int getPortraitCamearaDisplayOrientation(Context context, int cameraId, boolean isFrontCamera) {
        if (cameraId < 0 || cameraId > getCameraNumber()) {
            return -1;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int rotation = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (isFrontCamera) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static int[] findClosestFpsRange(Camera camera, int minFrameRate, int maxFrameRate) {
        minFrameRate *= 1000;
        maxFrameRate *= 1000;
        Camera.Parameters parameters = camera.getParameters();
        int minIndex = 0;
        int minDiff = Integer.MAX_VALUE;
        List<int[]> rangeList = parameters.getSupportedPreviewFpsRange();
        Log.d(TAG, "support preview fps range list: " + dumpFpsRangeList(rangeList));
        for (int i = 0; i < rangeList.size(); i++) {
            int[] fpsRange = rangeList.get(i);
            if (fpsRange.length != 2) {
                continue;
            }
            int minFps = fpsRange[0] / 1000;
            int maxFps = fpsRange[1] / 1000;
            int diff = Math.abs(minFps - minFrameRate) + Math.abs(maxFps - maxFrameRate);
            if (diff < minDiff) {
                minDiff = diff;
                minIndex = i;
            }
        }
        int[] result = rangeList.get(minIndex);
        return result;
    }

    private static String dumpFpsRangeList(List<int[]> rangeList) {
        String result = "";
        for (int[] range : rangeList) {
            if (range.length != 2) {
                continue;
            }
            result += "(" + range[0] + "," + range[1] + ") ";
        }
        return result;
    }

    public static Camera.Size findClosetPreviewSize(Camera camera, Point preferSize) {
        int preferX = preferSize.x;
        int preferY = preferSize.y;
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> allSupportSizes = parameters.getSupportedPreviewSizes();
        Log.d(TAG, "all support preview size: " + dumpPreviewSizeList(allSupportSizes));
        int minDiff = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < allSupportSizes.size(); i++) {
            Camera.Size size = allSupportSizes.get(i);
            int x = size.width;
            int y = size.height;

            int diff = Math.abs(x - preferX) + Math.abs(y - preferY);
            if (diff < minDiff) {
                minDiff = diff;
                index = i;
            }
        }

        Camera.Size size = allSupportSizes.get(index);
        return size;
    }

    private static String dumpPreviewSizeList(List<Camera.Size> sizes) {
        String result = "";
        for (Camera.Size size : sizes) {
            result += "(" + size.width + "," + size.height + ") ";
        }
        return result;
    }
}
