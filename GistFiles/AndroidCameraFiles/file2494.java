package br.com.testr.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.io.IOException;

import br.com.testr.AppApplication;
import br.com.testr.helper.CameraHelper;
import br.com.testr.helper.MediaRecorderHelper;
import br.com.testr.helper.DraggableViewRecordingHelper;


/**
 * Created by uzias on 10/3/16.
 */

public class ScreenRecorderService extends Service {

    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallback mediaProjectionCallback;
    private MediaRecorder mediaRecorder;
    private int displayWidth;
    private int mDisplayHeight;
    private int mDensityDpi;
    private WindowManager windowManager;
    private DraggableViewRecordingHelper draggableViewRecordingHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaRecorder = new MediaRecorder();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDensityDpi = metrics.densityDpi;
        displayWidth = metrics.widthPixels;
        mDisplayHeight = metrics.heightPixels;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        draggableViewRecordingHelper = new DraggableViewRecordingHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initRecorder();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecorderAndScreen();
    }

    private void initRecorder() {
        try {
            if (mediaProjectionCallback == null){
                mediaProjectionCallback = new MediaProjectionCallback();
            }
            mediaProjection = AppApplication.mediaProjection;
            if (mediaProjection == null){
                hideRecordingViewStopServicesShowError();
                return;
            }
            mediaProjection.registerCallback(mediaProjectionCallback, null);
            mediaRecorder = MediaRecorderHelper.configureRecorder(mediaRecorder, windowManager, displayWidth, mDisplayHeight, false, null);
            mediaRecorder.prepare();
            virtualDisplay = MediaRecorderHelper.createVirtualDisplay(mediaProjection, mediaRecorder, this, displayWidth, mDisplayHeight, mDensityDpi);
            mediaRecorder.start();
            draggableViewRecordingHelper.showView();
        } catch (IOException e) {
            e.printStackTrace();
            hideRecordingViewStopServicesShowError();
        }
    }

    private void destroyMediaProjection() {
        if (mediaProjection != null) {
            mediaProjection.unregisterCallback(mediaProjectionCallback);
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

    private void stopRecorderAndScreen() {
        try {
            mediaRecorder.stop();
        }catch (Exception e){
            e.printStackTrace();
            hideRecordingViewStopServicesShowError();
        }
        draggableViewRecordingHelper.hideView();
        mediaRecorder.reset();
        destroyMediaProjection();
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
    }

    private void hideRecordingViewStopServicesShowError(){
        CameraHelper.hideRecordingViewStopServicesShowError(this);
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            stopRecorderAndScreen();
        }
    }

}