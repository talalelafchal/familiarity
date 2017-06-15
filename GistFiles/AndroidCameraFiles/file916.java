package com.wta.videodemo;

/*
 * Copyright (C) 2012 The Android Open Source Project
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

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;


public class CameraManager {
    private static final String TAG = "CameraManager";
    private static CameraManager sCameraManager = new CameraManager();

    // Thread progress signals
    private ConditionVariable mSig = new ConditionVariable();

    private Camera.Parameters mParameters;
    private IOException mReconnectException;

    private static final int RELEASE = 1;
    private static final int RECONNECT = 2;
    private static final int UNLOCK = 3;
    private static final int LOCK = 4;
    private static final int SET_PREVIEW_TEXTURE_ASYNC = 5;
    private static final int START_PREVIEW_ASYNC = 6;
    private static final int STOP_PREVIEW = 7;
    private static final int SET_PREVIEW_CALLBACK_WITH_BUFFER = 8;
    private static final int ADD_CALLBACK_BUFFER = 9;
    private static final int AUTO_FOCUS = 10;
    private static final int CANCEL_AUTO_FOCUS = 11;
    private static final int SET_AUTO_FOCUS_MOVE_CALLBACK = 12;
    private static final int SET_DISPLAY_ORIENTATION = 13;
    private static final int SET_ZOOM_CHANGE_LISTENER = 14;
    //private static final int SET_FACE_DETECTION_LISTENER = 15;
    //private static final int START_FACE_DETECTION = 16;
    //private static final int STOP_FACE_DETECTION = 17;
    private static final int SET_ERROR_CALLBACK = 18;
    private static final int SET_PARAMETERS = 19;
    private static final int GET_PARAMETERS = 20;
    private static final int SET_PARAMETERS_ASYNC = 21;
    private static final int WAIT_FOR_IDLE = 22;
    private static final int SET_PREVIEW_DISPLAY_ASYNC = 23;
    private static final int SET_PREVIEW_CALLBACK = 24;
    private static final int ENABLE_SHUTTER_SOUND = 25;

    private Handler mCameraHandler;
    private CameraProxy mCameraProxy;
    private android.hardware.Camera mCamera;

    public static CameraManager instance() {
        return sCameraManager;
    }

    private CameraManager() {
        HandlerThread ht = new HandlerThread("Camera Handler Thread");
        ht.start();
        mCameraHandler = new CameraHandler(ht.getLooper());
    }

    private class CameraHandler extends Handler {
        CameraHandler(Looper looper) {
            super(looper);
        }

        /*@TargetApi(ApiHelper.VERSION_CODES.ICE_CREAM_SANDWICH)
        private void startFaceDetection() {
            mCamera.startFaceDetection();
        }*/

        /*@TargetApi(ApiHelper.VERSION_CODES.ICE_CREAM_SANDWICH)
        private void stopFaceDetection() {
            mCamera.stopFaceDetection();
        }*/

        /*@TargetApi(ApiHelper.VERSION_CODES.ICE_CREAM_SANDWICH)
        private void setFaceDetectionListener(Camera.FaceDetectionListener listener) {
            mCamera.setFaceDetectionListener(listener);
        }*/

        //@TargetApi(ApiHelper.VERSION_CODES.HONEYCOMB)
        private void setPreviewTexture(Object surfaceTexture) {
            try {
                mCamera.setPreviewTexture((SurfaceTexture) surfaceTexture);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        /*@TargetApi(ApiHelper.VERSION_CODES.JELLY_BEAN_MR1)
        private void enableShutterSound(boolean enable) {
            mCamera.enableShutterSound(enable);
        }*/

        /*
         * This method does not deal with the build version check.  Everyone should
         * check first before sending message to this handler.
         */
        @Override
        public void handleMessage(final Message msg) {
            try {
                switch (msg.what) {
                    case RELEASE:
                        mCamera.release();
                        mCamera = null;
                        mCameraProxy = null;
                        break;

                    case RECONNECT:
                        mReconnectException = null;
                        try {
                            mCamera.reconnect();
                        } catch (IOException ex) {
                            mReconnectException = ex;
                        }
                        break;

                    case UNLOCK:
                        mCamera.unlock();
                        break;

                    case LOCK:
                        mCamera.lock();
                        break;

                    case SET_PREVIEW_TEXTURE_ASYNC:
                        setPreviewTexture(msg.obj);
                        return;  // no need to call mSig.open()

                    case SET_PREVIEW_DISPLAY_ASYNC:
                        try {
                            mCamera.setPreviewDisplay((SurfaceHolder) msg.obj);
                        } catch(IOException e) {
                            throw new RuntimeException(e);
                        }
                        return;  // no need to call mSig.open()

                    case START_PREVIEW_ASYNC:
                        mCamera.startPreview();
                        return;  // no need to call mSig.open()

                    case STOP_PREVIEW:
                        mCamera.stopPreview();
                        break;

                    case SET_PREVIEW_CALLBACK_WITH_BUFFER:
                        mCamera.setPreviewCallbackWithBuffer(
                                (Camera.PreviewCallback) msg.obj);
                        break;

                    case ADD_CALLBACK_BUFFER:
                        mCamera.addCallbackBuffer((byte[]) msg.obj);
                        break;

                    case AUTO_FOCUS:
                        mCamera.autoFocus((Camera.AutoFocusCallback) msg.obj);
                        break;

                    case CANCEL_AUTO_FOCUS:
                        mCamera.cancelAutoFocus();
                        break;

                    case SET_AUTO_FOCUS_MOVE_CALLBACK:
                        setAutoFocusMoveCallback(mCamera, msg.obj);
                        break;

                    case SET_DISPLAY_ORIENTATION:
                        mCamera.setDisplayOrientation(msg.arg1);
                        break;

                    case SET_ZOOM_CHANGE_LISTENER:
                        mCamera.setZoomChangeListener(
                                (Camera.OnZoomChangeListener) msg.obj);
                        break;

                    /*case SET_FACE_DETECTION_LISTENER:
                        setFaceDetectionListener((Camera.FaceDetectionListener) msg.obj);
                        break;*/

                    /*case START_FACE_DETECTION:
                        startFaceDetection();
                        break;*/

                    /*case STOP_FACE_DETECTION:
                        stopFaceDetection();
                        break;*/

                    case SET_ERROR_CALLBACK:
                        mCamera.setErrorCallback((Camera.ErrorCallback) msg.obj);
                        break;

                    case SET_PARAMETERS:
                        mCamera.setParameters((Camera.Parameters) msg.obj);
                        break;

                    case GET_PARAMETERS:
                        mParameters = mCamera.getParameters();
                        break;

                    case SET_PARAMETERS_ASYNC:
                        mCamera.setParameters((Camera.Parameters) msg.obj);
                        return;  // no need to call mSig.open()

                    case SET_PREVIEW_CALLBACK:
                        mCamera.setPreviewCallback((Camera.PreviewCallback) msg.obj);
                        break;

                    /*case ENABLE_SHUTTER_SOUND:
                        enableShutterSound((msg.arg1 == 1) ? true : false);
                        break;*/

                    case WAIT_FOR_IDLE:
                        // do nothing
                        break;

                    default:
                        throw new RuntimeException("Invalid CameraProxy message=" + msg.what);
                }
            } catch (RuntimeException e) {
                if (msg.what != RELEASE && mCamera != null) {
                    try {
                        mCamera.release();
                    } catch (Exception ex) {
                        Log.e(TAG, "Fail to release the camera.");
                    }
                    mCamera = null;
                    mCameraProxy = null;
                }
                throw e;
            }
            mSig.open();
        }
    }


    private void setAutoFocusMoveCallback(android.hardware.Camera camera,
            Object cb) {
        camera.setAutoFocusMoveCallback((Camera.AutoFocusMoveCallback) cb);
    }

    // Open camera synchronously. This method is invoked in the context of a
    // background thread.
    CameraProxy cameraOpen(int cameraId) {
        // Cannot open camera in mCameraHandler, otherwise all camera events
        // will be routed to mCameraHandler looper, which in turn will call
        // event handler like Camera.onFaceDetection, which in turn will modify
        // UI and cause exception like this:
        // CalledFromWrongThreadException: Only the original thread that created
        // a view hierarchy can touch its views.
        mCamera = android.hardware.Camera.open(cameraId);
        if (mCamera != null) {
            mCameraProxy = new CameraProxy();
            return mCameraProxy;
        } else {
            return null;
        }
    }

    public class CameraProxy {
        private CameraProxy() {
            //Assert(mCamera != null);
        }

        public android.hardware.Camera getCamera() {
            return mCamera;
        }

        public void release() {
            mSig.close();
            mCameraHandler.sendEmptyMessage(RELEASE);
            mSig.block();
        }

        public void reconnect() throws IOException {
            mSig.close();
            mCameraHandler.sendEmptyMessage(RECONNECT);
            mSig.block();
            if (mReconnectException != null) {
                throw mReconnectException;
            }
        }

        public void unlock() {
            mSig.close();
            mCameraHandler.sendEmptyMessage(UNLOCK);
            mSig.block();
        }

        public void lock() {
            mSig.close();
            mCameraHandler.sendEmptyMessage(LOCK);
            mSig.block();
        }

        public void setPreviewTextureAsync(final SurfaceTexture surfaceTexture) {
            mCameraHandler.obtainMessage(SET_PREVIEW_TEXTURE_ASYNC, surfaceTexture).sendToTarget();
        }

        public void setPreviewDisplayAsync(final SurfaceHolder surfaceHolder) {
            mCameraHandler.obtainMessage(SET_PREVIEW_DISPLAY_ASYNC, surfaceHolder).sendToTarget();
        }

        public void startPreviewAsync() {
            mCameraHandler.sendEmptyMessage(START_PREVIEW_ASYNC);
        }

        public void stopPreview() {
            mSig.close();
            mCameraHandler.sendEmptyMessage(STOP_PREVIEW);
            mSig.block();
        }

        public void setPreviewCallback(final Camera.PreviewCallback cb) {
            mSig.close();
            mCameraHandler.obtainMessage(SET_PREVIEW_CALLBACK, cb).sendToTarget();
            mSig.block();
        }

        public void setPreviewCallbackWithBuffer(final Camera.PreviewCallback cb) {
            mSig.close();
            mCameraHandler.obtainMessage(SET_PREVIEW_CALLBACK_WITH_BUFFER, cb).sendToTarget();
            mSig.block();
        }

        public void addCallbackBuffer(byte[] callbackBuffer) {
            mSig.close();
            mCameraHandler.obtainMessage(ADD_CALLBACK_BUFFER, callbackBuffer).sendToTarget();
            mSig.block();
        }

        public void autoFocus(Camera.AutoFocusCallback cb) {
            mSig.close();
            mCameraHandler.obtainMessage(AUTO_FOCUS, cb).sendToTarget();
            mSig.block();
        }

        public void cancelAutoFocus() {
            mSig.close();
            mCameraHandler.sendEmptyMessage(CANCEL_AUTO_FOCUS);
            mSig.block();
        }


        public void takePicture(final Camera.ShutterCallback shutter, final Camera.PictureCallback raw,
                final Camera.PictureCallback postview, final Camera.PictureCallback jpeg) {
            mSig.close();
            // Too many parameters, so use post for simplicity
            mCameraHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCamera.takePicture(shutter, raw, postview, jpeg);
                    mSig.open();
                }
            });
            mSig.block();
        }

        public void takePicture2(final Camera.ShutterCallback shutter, final Camera.PictureCallback raw,
                final Camera.PictureCallback postview, final Camera.PictureCallback jpeg,
                final int cameraState, final int focusState) {
            mSig.close();
            // Too many parameters, so use post for simplicity
            mCameraHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCamera.takePicture(shutter, raw, postview, jpeg);
                    } catch (RuntimeException e) {
                        Log.w(TAG, "take picture failed; cameraState:" + cameraState
                                + ", focusState:" + focusState);
                        throw e;
                    }
                    mSig.open();
                }
            });
            mSig.block();
        }

        public void setDisplayOrientation(int degrees) {
            mSig.close();
            mCameraHandler.obtainMessage(SET_DISPLAY_ORIENTATION, degrees, 0)
                    .sendToTarget();
            mSig.block();
        }

        public void setZoomChangeListener(Camera.OnZoomChangeListener listener) {
            mSig.close();
            mCameraHandler.obtainMessage(SET_ZOOM_CHANGE_LISTENER, listener).sendToTarget();
            mSig.block();
        }


        /*public void startFaceDetection() {
            mSig.close();
            mCameraHandler.sendEmptyMessage(START_FACE_DETECTION);
            mSig.block();
        }*/

        /*public void stopFaceDetection() {
            mSig.close();
            mCameraHandler.sendEmptyMessage(STOP_FACE_DETECTION);
            mSig.block();
        }*/

        public void setErrorCallback(Camera.ErrorCallback cb) {
            mSig.close();
            mCameraHandler.obtainMessage(SET_ERROR_CALLBACK, cb).sendToTarget();
            mSig.block();
        }

        public void setParameters(Camera.Parameters params) {
            mSig.close();
            mCameraHandler.obtainMessage(SET_PARAMETERS, params).sendToTarget();
            mSig.block();
        }

        public void setParametersAsync(Camera.Parameters params) {
            mCameraHandler.removeMessages(SET_PARAMETERS_ASYNC);
            mCameraHandler.obtainMessage(SET_PARAMETERS_ASYNC, params).sendToTarget();
        }

        public Camera.Parameters getParameters() {
            mSig.close();
            mCameraHandler.sendEmptyMessage(GET_PARAMETERS);
            mSig.block();
            Camera.Parameters parameters = mParameters;
            mParameters = null;
            return parameters;
        }

        public void enableShutterSound(boolean enable) {
            mSig.close();
            mCameraHandler.obtainMessage(
                    ENABLE_SHUTTER_SOUND, (enable ? 1 : 0), 0).sendToTarget();
            mSig.block();
        }

        public void waitForIdle() {
            mSig.close();
            mCameraHandler.sendEmptyMessage(WAIT_FOR_IDLE);
            mSig.block();
        }
    }
}
