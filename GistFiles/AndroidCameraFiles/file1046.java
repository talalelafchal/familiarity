package com.wta.videodemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import roboguice.inject.InjectView;

/**
 * User: frankd Date: 3/26/13 Time: 3:41 PM supports 2.3+ only TODO add orientation change
 */
public class VideoCapture extends Activity
        implements SurfaceHolder.Callback {

    @InjectView(R.id.start_recording) Button bStartRecording;
    @InjectView(R.id.stop_recording) Button bStopRecording;

    public static final String LOG_VIDEO_CAPTURE = "Video Capture";

    int currentCamera = 0;

    MediaRecorder mMediaRecorder;
    SurfaceHolder mSurfaceHolder;
    boolean recording = false;
    String recordingSession = "";
    String currentOutputFile = "";
    CameraManager mCameraManager;
    CameraManager.CameraProxy mCameraProxy;
    int videoIndex;
    final int previewWidth = 1280;
    final int previewHeight = 720;
    final int fileFormat = MediaRecorder.OutputFormat.MPEG_4;
    final int videoEncoder = MediaRecorder.VideoEncoder.H264;
    final int audioEncoder = MediaRecorder.AudioEncoder.AAC;
    final String fileExtension = ".mp4";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.video_record);
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.video_preview_surface);

        initCamera();
        mSurfaceHolder = cameraView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFixedSize(previewWidth, previewHeight); //set size of preview surface
        //this call has been deprecated on newer devices, but is required for video recording on older devices.
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        bStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });
        bStopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
        mMediaRecorder = new MediaRecorder();
    }


    public void initCamera() {
        mCameraManager = CameraManager.instance();
        mCameraProxy = mCameraManager.cameraOpen(currentCamera);
        mCameraProxy.setDisplayOrientation(90);
        Camera.Parameters camParams = mCameraProxy.getParameters();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            camParams.setRecordingHint(
                    true); //this improves preview->recording transition performance
        }
        mCameraProxy.setParametersAsync(
                camParams);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mCameraProxy.getCamera().getParameters().set("cam_mode", 1);
        mCameraProxy.setPreviewDisplayAsync(holder);
        mCameraProxy.startPreviewAsync();
        mCameraProxy.unlock();
        mMediaRecorder.setCamera(mCameraProxy.getCamera());
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        stopRecording();
        stopPreview();
        if (recording) {
            mMediaRecorder.stop();
            recording = false;
        }
        mMediaRecorder.release();
        finish();
    }

    public void stopPreview() {
        mCameraProxy.stopPreview();
        mCameraProxy.release();
    }

    private void setupAndPrepareRecorder(MediaRecorder recorderToInit) {
        Log.d(LOG_VIDEO_CAPTURE, "Attempting to initialize MediaRecorder");
        recorderToInit.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorderToInit.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        CamcorderProfile profile = CamcorderProfile
                .get(CamcorderProfile.QUALITY_LOW); //some older phones do not define a QUALITY_HIGH, so QUALITY_LOW must be used by default

        profile.fileFormat = fileFormat;
        profile.videoCodec = videoEncoder;
        profile.audioCodec = audioEncoder;
        //TODO rework this to use as close to unversal info as possible
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            //these are the only values known to work on HTC evo
            profile.videoBitRate = 2000000;
            recorderToInit.setProfile(profile);
            int recordWidth = 640;
            int recordHeight = 384;
            recorderToInit.setVideoSize(recordWidth,
                    recordHeight);//old phones crash without this, new phones crash with it. its weird
        } else {
            profile = CamcorderProfile.get(currentCamera, CamcorderProfile.QUALITY_HIGH);
            recorderToInit.setProfile(profile);
        }

        //if this directory does not exist, start failed -4 is thrown
        currentOutputFile = "/sdcard/v/videocapture_" + recordingSession
                + fileExtension;
        recorderToInit.setOutputFile(currentOutputFile);
        recorderToInit.setMaxDuration(5000); // 5 seconds
        recorderToInit.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int i, int i2) {
                if (i == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    //duration set above has been reached. If you want the video to stop, end it now
                }
            }
        });
        recorderToInit.setPreviewDisplay(mSurfaceHolder.getSurface());
        try {
            recorderToInit.prepare();
        } catch (IllegalStateException e) {
            //TODO more robust error handling
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            //TODO more robust error handling
            e.printStackTrace();
            finish();
        }
        Log.d(LOG_VIDEO_CAPTURE, "Successfully setup and prepared MediaRecorder");
    }

    private void startRecording() {
        recording = true;
        recordingSession = String.valueOf(System.currentTimeMillis());
        setupAndPrepareRecorder(mMediaRecorder);
        mMediaRecorder.start();
        Log.d(LOG_VIDEO_CAPTURE, "Successfully started recording");
    }

    private void stopRecording() {
        recording = false;
        mMediaRecorder.reset();
        cameraDisconnectAndReconnect();
        mMediaRecorder.setCamera(mCameraProxy.getCamera());
        Log.d(LOG_VIDEO_CAPTURE, "Successfully stopped recording");
    }


    //this must be called each time the recorder is reset.
    //failing to do so will result in a "start failed: -19"
    private void cameraDisconnectAndReconnect() {
        try {
            mCameraProxy.reconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCameraProxy.unlock();
    }
}