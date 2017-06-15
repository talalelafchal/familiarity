package me.dontenvy.videotest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.RelativeLayout;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CameraView extends Fragment{

    private TextureView myTextureView = null;

    private RelativeLayout myRelativeLayout = null;

    private String myCameraId = null;

    private CameraDevice myCameraDevice = null;

    private CameraCaptureSession myCameraCaptureSession = null;

    private CaptureRequest.Builder myPreviewRequestBuilder;

    private CaptureRequest myPreviewRequest;

    private Handler myBackgroundHandler = null;

    private HandlerThread myBackgroundThread = null;

    private Semaphore myCameraOpenCloseLock =new Semaphore(1);

    private final TextureView.SurfaceTextureListener mySurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    openCamera();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            };

    private final CameraDevice.StateCallback myStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            myCameraOpenCloseLock.release();
            myCameraDevice = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            myCameraOpenCloseLock.release();
            camera.close();
            myCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            myCameraOpenCloseLock.release();
            camera.close();
            myCameraDevice = null;
            Activity activity = getActivity();
            if(activity != null){
                activity.finish();
            }
        }
    };

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;
    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;
    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    private int myState = STATE_PREVIEW;

    private CameraCaptureSession.CaptureCallback myCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {
    };

    public static CameraView newInstance(){
        CameraView cameraView = new CameraView();
        cameraView.setRetainInstance(true);
        return cameraView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        myTextureView = (TextureView) view.findViewById(R.id.texture);
        myRelativeLayout = (RelativeLayout) view.findViewById(R.id.slide_menu);


    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        if(myTextureView.isAvailable()){
            openCamera();
        }else{
            myTextureView.setSurfaceTextureListener(mySurfaceTextureListener);
        }

    }

    private void openCamera(){
        setUpCameraOutputs();
        Activity activity = getActivity();
        CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        try{
            if(!myCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)){
                throw new RuntimeException("Time Out Waiting For Camera Opening");
            }
            cameraManager.openCamera(myCameraId,myStateCallBack, myBackgroundHandler);
        }catch(CameraAccessException e){
            e.printStackTrace();
        }catch(InterruptedException e){
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    private void closeCamera(){
        try {
            myCameraOpenCloseLock.acquire();
            if (null != myCameraCaptureSession) {
                myCameraCaptureSession.close();
                myCameraCaptureSession = null;
            }
            if (null != myCameraDevice) {
                myCameraDevice.close();
                myCameraDevice = null;
            }
            /**if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }*/
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            myCameraOpenCloseLock.release();
        }
    }

    private void setUpCameraOutputs(){
        Activity activity = getActivity();
        CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        try{
            for (String cameraId : cameraManager.getCameraIdList()){
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
                        == CameraCharacteristics.LENS_FACING_FRONT){
                    continue;
                }

                myCameraId = cameraId;
                return;
            }
        }catch(CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession(){
        try{
            SurfaceTexture surfaceTexture = myTextureView.getSurfaceTexture();
            Surface surface = new Surface(surfaceTexture);

            myPreviewRequestBuilder
                    = myCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            myPreviewRequestBuilder.addTarget(surface);

            myCameraDevice.createCaptureSession(Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            if (null == myCameraDevice) {
                                return;
                            }

                            myCameraCaptureSession = session;

                            try{
                                myPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                myPreviewRequest = myPreviewRequestBuilder.build();
                                myCameraCaptureSession.setRepeatingRequest(myPreviewRequest,
                                        myCaptureCallback, myBackgroundHandler);
                            }catch (CameraAccessException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                        }
                    },null
            );
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void startBackgroundThread(){
        myBackgroundThread = new HandlerThread("CameraBackground");
        myBackgroundThread.start();
        myBackgroundHandler = new Handler(myBackgroundThread.getLooper());
    }

    private void stopBackgroundThread(){
        myBackgroundThread.quitSafely();
        try {
            myBackgroundThread.join();
            myBackgroundThread = null;
            myBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
