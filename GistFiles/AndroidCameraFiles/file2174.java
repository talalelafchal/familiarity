package app.gudarin.droidgaze.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.List;

/**
 * Created by Verachad W. on 5/13/2014.
 */
public class CameraWrapperView extends TextureView implements TextureView.SurfaceTextureListener, Camera.PreviewCallback{

    private Camera mCamera;
    private int mCameraId = -1;
    private int mFrameWidth = 640;
    private int mFrameHeight = 480;

    private Bitmap mCachedBitmap;

    public static final int CAMERA_BACK = 0;
    public static final int CAMERA_FRONT = 1;

    private Mat[] mFrameChain;
    private int mChainIdx = 0;
    private byte[] mBuffer;
    private JavaCameraFrame[] mCameraFrame;
    private boolean mStopThread;
    private Thread mThread;
    private boolean isSurfaceAvailable;

    public interface CameraWrapperListener {
        public void onPreviewInBackground(CameraBridgeViewBase.CvCameraViewFrame frame);
    }

    private CameraWrapperListener mListener;


    public CameraWrapperView(Context context) {
        super(context);
        init();
    }

    public CameraWrapperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraWrapperView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mCachedBitmap = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Bitmap.Config.ARGB_8888);

        setSurfaceTextureListener(this);
    }

    public void setCameraListener(CameraWrapperListener listener) {
        mListener = listener;
    }

    public void setCameraIndex(int index) {
        if (index == CAMERA_BACK ){
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }

    }

    private void openCamera() {
        if (mCameraId == -1) {
            mCamera = Camera.open();
        } else {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == mCameraId) {
                    try {
                        mCamera = Camera.open(camIdx);
                    } catch (RuntimeException e) {
                        Log.e("", "Camera failed to open: " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    private void connectCamera(SurfaceTexture surfaceTexture) {
        openCamera();
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(mFrameWidth, mFrameHeight);
        params.setRecordingHint(true);
        List<String> FocusModes = params.getSupportedFocusModes();
        if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(params);

        int size = mFrameWidth * mFrameHeight;
        size = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
        mBuffer = new byte[size];

        mCamera.addCallbackBuffer(mBuffer);
        mCamera.setPreviewCallbackWithBuffer(this);

        mFrameChain = new Mat[2];
        mFrameChain[0] = new Mat(mFrameHeight + (mFrameHeight / 2), mFrameWidth, CvType.CV_8UC1);
        mFrameChain[1] = new Mat(mFrameHeight + (mFrameHeight / 2), mFrameWidth, CvType.CV_8UC1);

        mCameraFrame = new JavaCameraFrame[2];
        mCameraFrame[0] = new JavaCameraFrame(mFrameChain[0], mFrameWidth, mFrameHeight);
        mCameraFrame[1] = new JavaCameraFrame(mFrameChain[1], mFrameWidth, mFrameHeight);

        mStopThread = false;
        mThread = new Thread(new CameraWorker());
        mThread.start();
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enableView(){
        setEnabled(true);
        if (isSurfaceAvailable)
            connectCamera(getSurfaceTexture());
    }

    public void disableView() {
        setEnabled(false);
        try {
            mStopThread = true;
            synchronized (this) {
                this.notify();
            }
            if (mThread != null)
                mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mThread =  null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        isSurfaceAvailable = true;
        if (isEnabled()) {
            connectCamera(surfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {

        isSurfaceAvailable = false;
        mStopThread = true;

        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
        mCamera.release();

        if (mFrameChain != null) {
            mFrameChain[0].release();
            mFrameChain[1].release();
        }
        if (mCameraFrame != null) {
            mCameraFrame[0].release();
            mCameraFrame[1].release();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        synchronized (this) {
            mFrameChain[1 - mChainIdx].put(0, 0, data);
            this.notify();
        }
        if (mCamera != null) {
            mCamera.addCallbackBuffer(mBuffer);
        }

    }

    private class JavaCameraFrame implements CameraBridgeViewBase.CvCameraViewFrame {
        public Mat gray() {
            return mYuvFrameData.submat(0, mHeight, 0, mWidth);
        }

        public Mat rgba() {
            Imgproc.cvtColor(mYuvFrameData, mRgba, Imgproc.COLOR_YUV2BGR_NV12, 4);
            return mRgba;
        }

        public JavaCameraFrame(Mat Yuv420sp, int width, int height) {
            super();
            mWidth = width;
            mHeight = height;
            mYuvFrameData = Yuv420sp;
            mRgba = new Mat();
        }

        public void release() {
            mRgba.release();
        }

        private Mat mYuvFrameData;
        private Mat mRgba;
        private int mWidth;
        private int mHeight;
    };


    private class CameraWorker implements Runnable {

        public void run() {
            do {
                synchronized (CameraWrapperView.this) {
                    try {
                        CameraWrapperView.this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!mStopThread) {
                    if (!mFrameChain[mChainIdx].empty() && mListener != null) {
                        mListener.onPreviewInBackground(mCameraFrame[mChainIdx]);
                    }
                    mChainIdx = 1 - mChainIdx;
                }
            } while (!mStopThread);
        }
    }
}
