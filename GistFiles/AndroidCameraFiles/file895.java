package com.vid.colortapedetectorapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.anhong.imorev.FragmentAddTest;
import com.anhong.imorev.MApplication;
import com.anhong.imorev.R;
import com.anhong.imorev.tool.ComomUtils;
import com.anhong.imorev.tool.DialogTool;
import com.einhron.reviewking.utility.BaseFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

public class CameraPreviewActivity extends BaseFragment implements SurfaceHolder.Callback, Camera.PreviewCallback {
    // frame rate
    static final int FRAMES_PER_SEC = 50;
    private static final String TAG = "CameraPreviewActivity";
    private static final int FOCUS_AREA_SIZE = 300;
    // preview min max edge size
    private static final int MIN_SIZE = 500; // 360
    private static final int MAX_SIZE = 1000; // 544
    private static final int MEDIA_TYPE_IMAGE = 1;
    static Rect[] colorTapeDetectedForImage;
    // camera related
    private static Camera camera;
    private static boolean isJNILibraryInit = false;
    // va ctd init
    private static int detectedMaxNumber = 50;
    private static int[] numberOfTapeDetectedForImage = new int[1];
    private static int[] outputArrayAngle = new int[detectedMaxNumber];

    static {
        System.loadLibrary("JNIColorTapeDetection");
    }

    // va ctd exec
    // detected color tape(s)
    Rect[] colorTapeDetected;
    private Context context;
    private int cameraId = 0;
    private SurfaceHolder surfaceHolder;
    private int displayOrientation = 0;
    private boolean isFacingFront = false;
    private double scale = 1.0;
    // camera preview
    private int width;
    private int height;
    private int degree;
    private int direction;
    // JNIColorTapeDetection
    private int imageFormat;
    private int[] contextId = new int[1];
    private int[] contextIdForImage = new int[1];
    private int[] outputArrayRectXForImage = new int[detectedMaxNumber];
    private int[] outputArrayRectYForImage = new int[detectedMaxNumber];
    private int[] outputArrayRectWidthForImage = new int[detectedMaxNumber];
    private int[] outputArrayRectHeightForImage = new int[detectedMaxNumber];
    private int[] outputArrayAngleForImage = new int[detectedMaxNumber];
    private int[] outputArrayColorTypeForImage = new int[detectedMaxNumber];
    private int[] outputArrayConfidenceForImage = new int[detectedMaxNumber];
    private int[] numberOfTapeDetected = new int[1];
    private int[] outputArrayRectX = new int[detectedMaxNumber];
    private int[] outputArrayRectY = new int[detectedMaxNumber];
    private int[] outputArrayRectWidth = new int[detectedMaxNumber];
    private int[] outputArrayRectHeight = new int[detectedMaxNumber];
    private int[] outputArrayColorType = new int[detectedMaxNumber];
    private int[] outputArrayConfidence = new int[detectedMaxNumber];
    // bitmap for showing
    private Bitmap colorTapeViewBitmap = null;
    private ByteBuffer rgbBuffer = null;
    // UI
    private SurfaceView surfaceView = null;
    private ToggleButton flashToggleButton;
    private Button takePicButton;
    private ProgressBar spinner;
    private ColorTapeRectView colorTapeRectView = null;
    private AlertDialog.Builder dialog;
    private Handler mValidateHandler = null;
    private View v;
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {

        public void onAutoFocus(boolean success, Camera camera) {

            Log.i(TAG, String.format("focus success: %b.", success));
        }
    };

    public static CameraPreviewActivity getInstance(String strCategroyUUID, boolean isValidate) {
        CameraPreviewActivity fragment = new CameraPreviewActivity();
        Bundle bundle = new Bundle();
        bundle.putString(MApplication.KEY_CATEGORY_ID, strCategroyUUID);
        bundle.putBoolean(MApplication.KEY_VALIDATE, isValidate);

        fragment.setArguments(bundle);

        return fragment;
    }

    public static CameraPreviewActivity getInstance(boolean isValidate) {
        CameraPreviewActivity fragment = new CameraPreviewActivity();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MApplication.KEY_VALIDATE, isValidate);

        fragment.setArguments(bundle);

        return fragment;
    }

    private static int degreeToCam(int degree) {

        int dirt = 1;

        if (degree == 90) {
            dirt = 2;
        } else if (degree == 180) {
            dirt = 3;
        } else if (degree == 270) {
            dirt = 4;
        }

        return dirt;
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "ColorTapeDetectorApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("ColorTapeDetectorApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = HelpUtil.getDateFormatString(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".png");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onDestroy() {
        stopCamera();
        releaseCamera();
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_validate, container, false);
        context = v.getContext();
        initialView(v);
        initSurfaceView(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ComomUtils.instance.logme(this, "onResume()");
        if (v != null) {
            if (initCamera(surfaceHolder)) {
                startCamera();
            }
        }
    }

    private void initSurfaceView(View vParent) {
        surfaceView = (SurfaceView) vParent.findViewById(R.id.cameraPreviewSurfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    camera.autoFocus(null);
                }
                return true;
            }
        });
    }

    private boolean setCameraId(int cameraId) {
        boolean ret = false;

        if (cameraId < Camera.getNumberOfCameras()) {
            this.cameraId = cameraId;
            ret = true;
        } else {
            this.cameraId = 0;
        }

        return ret;
    }

    private void initialView(final View vParent) {
        if (vParent == null) {
            ComomUtils.instance.logme(this, "initialView() vParent==null");
            return;
        }
        vParent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        dialog = new AlertDialog.Builder(getActivity());

        colorTapeRectView = (ColorTapeRectView) vParent.findViewById(R.id.colorTapeSurfaceView);
        colorTapeRectView.setZOrderMediaOverlay(true);

        spinner = (ProgressBar) vParent.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        flashToggleButton = (ToggleButton) vParent.findViewById(R.id.flashToggleButton);
        flashToggleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) {
                    setFlash(true);
                } else {
                    setFlash(false);
                }
            }
        });

        takePicButton = (Button) vParent.findViewById(R.id.takePicButton);
        takePicButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSnapPhoto(vParent);
            }
        });

        //一般便是關閉mask
        ImageView ivMask = (ImageView) vParent.findViewById(R.id.ivMask);
        Bundle bundle = this.getArguments();
        if (!bundle.getBoolean(MApplication.KEY_VALIDATE)) {
            ivMask.setVisibility(View.INVISIBLE);
        }

        VACTDSetParam(contextId[0], 15);
    }

    public void takeSnapPhoto(View vParent) {
        ComomUtils.instance.logme(this, "takeSnapPhoto()");
        camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

                // CTD init
                int initReturnValue = VACTDInit(contextIdForImage, width,
                        height, width * 2, 2, direction, 50, 1);
                Log.d(TAG,
                        "JNI contextIdForImage is "
                                + String.valueOf(contextIdForImage));
                Log.d(TAG, "init return value " + initReturnValue);

                VACTDSetParam(contextId[0], 10);

                if (colorTapeDetectedForImage == null) {
                    colorTapeDetectedForImage = new Rect[detectedMaxNumber];
                    for (int i = 0; i < detectedMaxNumber; i++) {
                        colorTapeDetectedForImage[i] = new Rect();
                    }
                }

                // CTD
                int execReturnValue = VACTDExec(contextIdForImage[0], data,
                        width, height, numberOfTapeDetectedForImage,
                        outputArrayRectXForImage, outputArrayRectYForImage,
                        outputArrayRectWidthForImage,
                        outputArrayRectHeightForImage,
                        outputArrayAngleForImage, outputArrayColorTypeForImage,
                        outputArrayConfidenceForImage);

                for (int i = 0; i < numberOfTapeDetectedForImage[0]; i++) {

                    colorTapeDetectedForImage[i].set(
                            outputArrayRectXForImage[i],
                            outputArrayRectYForImage[i],
                            (outputArrayRectXForImage[i]
                                    + outputArrayRectWidthForImage[i] - 1),
                            (outputArrayRectYForImage[i]
                                    + outputArrayRectHeightForImage[i] - 1));

                    Log.d(TAG, "colorTapeDetectedForImage x "
                            + outputArrayRectXForImage[i]
                            + " y "
                            + outputArrayRectYForImage[i]
                            + " width "
                            + (outputArrayRectXForImage[i]
                            + outputArrayRectWidthForImage[i] - 1)
                            + " height "
                            + (outputArrayRectYForImage[i]
                            + outputArrayRectHeightForImage[i] - 1)
                            + " degrees " + outputArrayAngleForImage[i]);
                }

                Camera.Parameters parameters = camera.getParameters();
                int format = parameters.getPreviewFormat();
                // YUV formats require more conversion
                if (format == ImageFormat.NV21) {
                    int w = parameters.getPreviewSize().width;
                    int h = parameters.getPreviewSize().height;
                    // Get the YuV image
                    YuvImage yuvImage = new YuvImage(data, format, w, h, null);
                    // Convert YuV to Jpeg
                    Rect rect = new Rect(0, 0, w, h);
                    ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(rect, 100, output_stream);
                    byte[] byt = output_stream.toByteArray();
                    File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                    if (pictureFile == null) {
                        Log.d(TAG,
                                "Error creating media file, check storage permissions: ");
                        return;
                    }
                    try {
                        Bitmap bitmapOriginal = BitmapFactory.decodeByteArray(
                                byt, 0, byt.length);

                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);

                        Bitmap bitmapRotate = Bitmap.createBitmap(
                                bitmapOriginal, 0, 0,
                                bitmapOriginal.getWidth(),
                                bitmapOriginal.getHeight(), matrix, true);
                        ByteArrayOutputStream blob = new ByteArrayOutputStream();
                        bitmapRotate.compress(CompressFormat.JPEG, 100, blob);
                        byte[] bitmapdata = blob.toByteArray();

                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        fos.write(bitmapdata);
                        fos.close();
                    } catch (Exception e) {
                        Log.d(TAG, "Error accessing file: " + e.getMessage());
                    }

                    VACTDFree(contextIdForImage[0]);

                    boolean isValidate = CameraPreviewActivity.this
                            .getArguments().getBoolean(
                                    MApplication.KEY_VALIDATE);
                    if (isValidate) {
                        // 驗證程序
                        gotoFirstValidate(pictureFile);

                    } else {
                        // 非驗證程序，一般拍照辨識
                        if (numberOfTapeDetectedForImage[0] > 0) {
                            gotoAddTest(pictureFile);
                        } else {
                            dialog.setTitle("沒有偵測結果");
                            dialog.setMessage("沒有偵測到色帶");
                            dialog.setPositiveButton("確定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialoginterface,
                                                int i) {
                                        }
                                    });
                            dialog.show();

                            VACTDFree(contextIdForImage[0]);

                            if (initCamera(surfaceHolder)) {
                                startCamera();
                            }
                        }

                    }
                }

            }
        });
    }

    /**
     * 重新啟動辨識相機
     */
    public void resetCamera(Context context) {

        /*
        if(v==null){
            ComomUtils.instance.logme(this, "resetCamera() v==null");

            //v = LayoutInflater.from(context).inflate(R.layout.fragment_validate, null, false);
            //initialView(v);
            //stopCamera();
            //releaseCamera();
            //FragmentHandler.instance.replace(CameraPreviewActivity.getInstance(true));
            return;
        }
        */
        //initSurfaceView(v);
        if (surfaceHolder == null) {
            ComomUtils.instance.logme(this, "resetCamera()  surfaceHolder==null");
            return;
        }
        VACTDFree(contextIdForImage[0]);
        if (initCamera(surfaceHolder)) {

            startCamera();

        }
    }

    private boolean initCamera(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        try {

            Log.i(TAG, "initCamera");

            stopCamera();
            releaseCamera();

            // camera = Camera.open();
            camera = Camera.open(cameraId);

            if (camera != null) {

                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(cameraId, cameraInfo);
                displayOrientation = cameraInfo.orientation;

                Camera.Parameters cameraParams = camera.getParameters();


                // preview size
                View parentView = ((View) surfaceView.getParent());
                double dispAspect = (double) parentView.getWidth()
                        / parentView.getHeight();


                Camera.Size bestSize = camera.new Size(1280, 720);

                cameraParams.setPreviewSize(bestSize.width, bestSize.height);
                cameraParams.setPictureSize(bestSize.width, bestSize.height);

                if (displayOrientation == 90 || displayOrientation == 270) {
                    scale = (double) surfaceView.getWidth() / bestSize.height;
                } else {
                    scale = (double) surfaceView.getWidth() / bestSize.width;
                }
                //cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
// focus mode
                setFocusMode(cameraParams);


                // Find closest FPS
                int closestRange[] = findClosestFpsRange(FRAMES_PER_SEC,
                        cameraParams);

                cameraParams.setPreviewFpsRange(
                        closestRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                        closestRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);

                camera.setParameters(cameraParams);
                camera.setDisplayOrientation(getFitRotateDegreeToCamera());
                camera.setPreviewDisplay(holder);
                camera.setPreviewCallback(this);

                int[] fpsrange = new int[2];
                cameraParams.getPreviewFpsRange(fpsrange);

            } else {
                DialogTool.instance.warnDialog("camera == null");
                return false;
            }
        } catch (Exception e) {

            ComomUtils.instance.logme(this, "initCamera Exception: " + e);

            releaseCamera();
            return false;
        }

        return true;
    }

    private void setFocusMode(Parameters cameraParams) {
        List<String> list = cameraParams.getSupportedFocusModes();

        for (String f : list) {
            if (f.equals(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                ComomUtils.instance.logme(this, "getSupportedFocusModes:FOCUS_MODE_CONTINUOUS_VIDEO");

                cameraParams.setFocusMode(f);
                autoFocusCallback = null;
                break;
            }
            if (f.equals(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                ComomUtils.instance.logme(this, "getSupportedFocusModes:FOCUS_MODE_CONTINUOUS_PICTURE");
                cameraParams.setFocusMode(f);
                autoFocusCallback = null;
                break;
            }
            if (f.equals(Parameters.FOCUS_MODE_AUTO)) {
                ComomUtils.instance.logme(this, "getSupportedFocusModes:FOCUS_MODE_AUTO");
                cameraParams.setFocusMode(f);
                camera.autoFocus(autoFocusCallback);
                autoFocusCallback = null;
                break;
            }
            if (f.equals(Parameters.FOCUS_MODE_MACRO)) {
                ComomUtils.instance.logme(this, "getSupportedFocusModes:FOCUS_MODE_MACRO");
                cameraParams.setFocusMode(f);
                camera.autoFocus(autoFocusCallback);
                autoFocusCallback = null;
                break;
            }
        }
    }

    private int[] findClosestFpsRange(int fps, Camera.Parameters params) {
        List<int[]> supportedFpsRanges = params.getSupportedPreviewFpsRange();
        int[] closestRange = supportedFpsRanges.get(0);
        int fpsk = fps * 1000;
        int minDiff = 1000000;
        for (int[] range : supportedFpsRanges) {
            int low = range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
            int high = range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
            if (low <= fpsk && high >= fpsk) {
                int diff = (fpsk - low) + (high - fpsk);
                if (diff < minDiff) {
                    closestRange = range;
                    minDiff = diff;
                }
            }
        }
        Log.i("CC", "Found closest range: "
                + closestRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] + " - "
                + closestRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        return closestRange;
    }

    private int getFitRotateDegree(boolean isFacingFront) {

        int degree = 0;
        int rotation = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                if (isFacingFront) {
                    degree = 270;
                } else {
                    degree = 90;
                }
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                if (isFacingFront) {
                    degree = 90;
                } else {
                    degree = 270;
                }
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }

        return degree;
    }

    private int getFitRotateDegreeToCamera() {

        int degree = 0;
        int rotation = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }

        return degree;
    }

    private boolean startCamera() {
        if (camera != null) {

            try {

                ComomUtils.instance.logme(this, "startCamera");
                setFocusMode(camera.getParameters());
                camera.startPreview();

            } catch (Exception e) {

                Log.e(TAG, "startCamera: " + e);
                return false;
            }
        }

        return true;
    }

    private void stopCamera() {
        if (camera != null) {
            if (contextIdForImage != null) {
                VACTDFree(contextIdForImage[0]);
            }

            camera.setPreviewCallback(null);
            ComomUtils.instance.logme(this, "stopCamera()");
            camera.stopPreview();
        }
    }

    private void releaseCamera() {
        if (camera != null) {

            Log.i(TAG, "releaseCamera");

            camera.release();
            camera = null;
        }
    }

    private void gotoAddTest(File mediaFile) {
        //轉送到新增考題
        Uri uri = Uri.fromFile(mediaFile);
        String url = uri.toString()
                .substring(uri.toString().indexOf("///") + 2);

        Bundle bundle = CameraPreviewActivity.this.getArguments();
        FragmentAddTest fragment = FragmentAddTest.getInstance(
                url,
                bundle.getString(MApplication.KEY_CATEGORY_ID),
                getImageDetectedRects(),
                getImageDetectedRectDegrees(),
                getNumberOfTapeDetectedForImage());

        stopCamera();
        goBackFragment();
        changeAddFragment(fragment);
    }

    private void gotoFirstValidate(File mediaFile) {
        Uri uri = Uri.fromFile(mediaFile);
        String url = uri.toString()
                .substring(uri.toString().indexOf("///") + 2);

        if (mValidateHandler != null && getImageDetectConfidences().length > 0) {
            int[] arrayConfidence = getImageDetectConfidences();

            Message msg = new Message();
            msg.what = arrayConfidence[0];
            msg.obj = url;

            mValidateHandler.dispatchMessage(msg);
        } else {
            int[] arrayConfidence = getImageDetectConfidences();

            Message msg = new Message();
            msg.what = 0;
            msg.obj = url;
            if (mValidateHandler != null) {
                mValidateHandler.dispatchMessage(msg);
            }

        }

        goBackFragment();
    }

    /**
     * 首頁驗證Handler
     *
     * @param handler
     */
    public void setValidateHandler(Handler handler) {
        if (mValidateHandler != null) {
            mValidateHandler.removeCallbacksAndMessages(null);
        }
        mValidateHandler = handler;
    }

    private Rect[] getImageDetectedRects() {
        return colorTapeDetectedForImage;
    }

    private int[] getImageDetectedRectDegrees() {
        return outputArrayAngle;
    }

    private int getNumberOfTapeDetectedForImage() {
        return numberOfTapeDetectedForImage[0];
    }

    private int[] getImageDetectConfidences() {
        return outputArrayConfidence;
    }

    public void setFlash(boolean isFlashOn) {

        Camera.Parameters cameraParams = camera.getParameters();

        if (isFlashOn) {
            cameraParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
            flashToggleButton.setText(this.getString(R.string.flash_off));
        } else {
            cameraParams.setFlashMode(Parameters.FLASH_MODE_OFF);
            flashToggleButton.setText(this.getString(R.string.flash_on));
        }

        camera.setParameters(cameraParams);
    }

    @Override
    protected void onFragmentResume() {
        ComomUtils.instance.logme(this, "onFragmentResume()");
        onResume();
    }

    @Override
    protected boolean onFragmentKeyDown() {
        ComomUtils.instance.logme(this, "onFragmentKeyDown() ");
        //stopCamera();
        //releaseCamera();
        return false;
    }

    public int getDisplayOrientation() {

        return displayOrientation;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCamera();
        releaseCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        degree = getDisplayOrientation();
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        width = size.width;
        height = size.height;

//		Log.d(TAG, "1111 width " + width + " height " + height);

        direction = degreeToCam(degree);

        // CTD init
        if (isJNILibraryInit == false) {
            int initReturnValue = VACTDInit(contextId, width, height,
                    width * 2, 2, direction, 50, 2);

//			Log.d(TAG, "JNI contextId is " + String.valueOf(contextId));
//			Log.d(TAG, "init return value " + initReturnValue);
//			Log.d(TAG, "2222 width " + width + " height " + height);

            isJNILibraryInit = true;
        }

        if (colorTapeDetected == null) {
            colorTapeDetected = new Rect[detectedMaxNumber];
            for (int i = 0; i < detectedMaxNumber; i++) {
                colorTapeDetected[i] = new Rect();
            }
        }

        // CTD
        int execReturnValue = VACTDExec(contextId[0], data, width, height,
                numberOfTapeDetected, outputArrayRectX, outputArrayRectY,
                outputArrayRectWidth, outputArrayRectHeight, outputArrayAngle,
                outputArrayColorType, outputArrayConfidence);

        if (numberOfTapeDetected[0] == 0) {
            for (int i = 0; i < detectedMaxNumber; i++) {
                colorTapeDetected[i].set(0, 0, 0, 0);
            }
        } else {
            for (int i = 0; i < numberOfTapeDetected[0]; i++) {
                colorTapeDetected[i].set(outputArrayRectX[i],
                        outputArrayRectY[i], (outputArrayRectX[i]
                                + outputArrayRectWidth[i] - 1),
                        (outputArrayRectY[i] + outputArrayRectHeight[i] - 1));
            }
        }

//		Log.d("Joey", "detected " + numberOfTapeDetected[0]);

        int bitmapWidth = 0;
        int bitmapHeight = 0;
        if (degree == 90 || degree == 270) {
            bitmapWidth = height;
            bitmapHeight = width;
        } else {
            bitmapWidth = width;
            bitmapHeight = height;
        }

//		Log.i(TAG, String.format("Bitmap size: %d * %d", bitmapWidth,
//				bitmapHeight));

        if (colorTapeViewBitmap == null
                || colorTapeViewBitmap.getWidth() != bitmapWidth
                || colorTapeViewBitmap.getHeight() != bitmapHeight) {

            if (colorTapeViewBitmap != null) {
                colorTapeViewBitmap.recycle();
                colorTapeViewBitmap = null;
            }

            Log.i(TAG, String.format("video size: %d * %d", width, height));

            colorTapeViewBitmap = Bitmap.createBitmap(bitmapWidth,
                    bitmapHeight, Bitmap.Config.ARGB_8888);
            colorTapeViewBitmap.setHasAlpha(false);

            rgbBuffer = ByteBuffer.allocate(colorTapeViewBitmap.getByteCount());

            int colorTapeViewWidth = 0;
            int colorTapeViewHeight = 0;

            if (surfaceView != null) {
                colorTapeViewWidth = surfaceView.getWidth();
                colorTapeViewHeight = surfaceView.getHeight();
            } else {
                View parentView = ((View) surfaceView.getParent());
                double dispAspect = (double) parentView.getWidth()
                        / parentView.getHeight();
                double videoAspect = (double) width / height;
                if (videoAspect < dispAspect) {
                    colorTapeViewWidth = (int) ((float) parentView.getHeight() * videoAspect);
                    colorTapeViewHeight = parentView.getHeight();
                } else {
                    colorTapeViewWidth = parentView.getWidth();
                    colorTapeViewHeight = (int) ((float) parentView.getWidth() / videoAspect);
                }
            }
            colorTapeRectView.setSize(colorTapeViewWidth, colorTapeViewHeight);

            Log.i(TAG, String.format("colorTapeRectView size: %d * %d",
                    colorTapeViewWidth, colorTapeViewHeight));
        }

        byte[] bytes = rgbBuffer.array();

        YUVToRGB(data, width, height, direction, bytes);

        rgbBuffer.rewind();
        colorTapeViewBitmap.copyPixelsFromBuffer(rgbBuffer);

        Bundle bundle = this.getArguments();
        if (bundle.getBoolean(MApplication.KEY_VALIDATE)) {
            colorTapeRectView.draw(
                    colorTapeViewBitmap,
                    colorTapeDetected,
                    outputArrayAngle,
                    numberOfTapeDetected[0],
                    false);
        } else {
            colorTapeRectView.draw(
                    colorTapeViewBitmap,
                    colorTapeDetected,
                    outputArrayAngle,
                    numberOfTapeDetected[0],
                    true);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

        Log.d(TAG, String.format("surfaceChanged: %d * %d", width, height));
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d(TAG, String.format("surfaceCreated"));

        setCameraId(cameraId);

        if (initCamera(arg0)) {
            startCamera();
        }


        /*
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                camera.autoFocus(autoFocusCallback);
                return true;
            }
        });
        */


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        stopCamera();
        releaseCamera();

    }

    // Native JNI
    //
    public native int VACTDSetParamMinArea(int contextId, int minArea);

    public native int VACTDInit(int[] contextId, int imageWidth,
                                int imageHeight, int imageWidthStep, int imageChannelNumber,
                                int direction, int outputArraySize, int executeMode);

    public native int VACTDExec(int contextId, byte[] NV21Data, int imageWidth,
                                int imageHeight, int[] outputNumber, int[] outputArrayRectX,
                                int[] outputArrayRectY, int[] outputArrayRectWidth,
                                int[] outputArrayRectHeight, int[] outputArrayAngle,
                                int[] outputArrayColorType, int[] outputArrayConfidence);

    public native int VACTDFree(int contextId);

    public native int VACTDSetParam(int contextId, int threshold);

    public native int YUVToRGB(byte[] yuvData, int imgWidth, int imgHeight,
                               int direction, byte[] rgbData);
}
