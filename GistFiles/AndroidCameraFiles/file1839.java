/*
 * Copyright (C) 2008 ZXing authors
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

package com.ctrlsmart.fpcx;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.ctrlsmart.captureTool.CameraManager;
import com.ctrlsmart.captureTool.DecodeHandler;
import com.ctrlsmart.captureTool.DecodeThread;
import com.ctrlsmart.captureTool.OcrResult;
import com.ctrlsmart.captureTool.OcrResultFailure;

//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.DecodeHintType;
//import com.google.zxing.Result;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureActivityHandler extends Handler {

  private static final String TAG = CaptureActivityHandler.class.getSimpleName();

  private final CaptureActivity activity;
  private final DecodeThread decodeThread;
  private State state;
  private final CameraManager cameraManager;

  private enum State {
    PREVIEW,
    SUCCESS,
    DONE,
    CONTINUOUS_PAUSED,
    CONTINUOUS
  }

  CaptureActivityHandler(CaptureActivity activity,
//                         Collection<BarcodeFormat> decodeFormats,
//                         Map<DecodeHintType,?> baseHints,
//                         String characterSet,
                         CameraManager cameraManager) {
    this.activity = activity;
    this.cameraManager = cameraManager;
    Log.e("CaptureActivityHandler", "struct CaptureActivityHandler");
    // Start ourselves capturing previews (and decoding if using continuous recognition mode).
    cameraManager.startPreview();

    decodeThread = new DecodeThread(activity);
    decodeThread.start();


      state = State.CONTINUOUS;

      // Show the shutter and torch buttons
//      activity.setButtonVisibility(true);

      // Display a "be patient" message while first recognition request is running
      activity.setStatusViewForContinuous();

      restartOcrPreviewAndDecode();

  }

  @Override
  public void handleMessage(Message message) {
    switch (message.what) {
      case R.id.restart_preview:
//        restartOcrPreview();

        restartPreviewAndDecode();
        break;
      case R.id.ocr_continuous_decode_failed:
        DecodeHandler.resetDecodeState();
        Log.w(TAG, "R.id.ocr_continuous_decode_failed");
        try {
          activity.handleOcrContinuousDecode((OcrResultFailure) message.obj);
        } catch (NullPointerException e) {
          Log.w(TAG, "got bad OcrResultFailure", e);
        }
        if (state == State.CONTINUOUS) {
          restartOcrPreviewAndDecode();
//          restartPreviewAndDecode();
        }
        break;
      case R.id.ocr_continuous_decode_succeeded:
        DecodeHandler.resetDecodeState();
        boolean flag = ((OcrResult) message.obj).getResultFlag();
        try {

          if(flag){
            activity.handleOcrDecode((OcrResult) message.obj);
          }else {
            activity.handleOcrContinuousDecode((OcrResult) message.obj);
          }
        } catch (NullPointerException e) {
          // Continue
        }
        if (state == State.CONTINUOUS && !flag) {
          restartOcrPreviewAndDecode();
//          restartPreviewAndDecode();
        }
        break;
      case R.id.ocr_decode_succeeded:
        state = State.SUCCESS;
//        activity.setShutterButtonClickable(true);
        activity.handleOcrDecode((OcrResult) message.obj);
        break;
      case R.id.ocr_decode_failed:
        state = State.PREVIEW;
//        activity.setShutterButtonClickable(true);
        Toast toast = Toast.makeText(activity.getBaseContext(), "OCR failed. Please try again.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
        break;

//      case R.id.restart_preview:
//        restartPreviewAndDecode();
//        break;
      case R.id.decode_succeeded:
        state = State.SUCCESS;
        Bundle bundle = message.getData();
        Bitmap barcode = null;
        float scaleFactor = 1.0f;
        if (bundle != null) {
          byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
          if (compressedBitmap != null) {
            barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
            // Mutable copy:
            barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
          }
          scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);          
        }
        activity.handleOcrDecode((OcrResult)message.obj);
        break;
      case R.id.decode_failed:
        // We're decoding as fast as possible, so when one decode fails, start another.
        state = State.PREVIEW;
        cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        break;
      case R.id.return_scan_result:
        activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
        activity.finish();
        break;
      case R.id.launch_product_query:
        String url = (String) message.obj;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setData(Uri.parse(url));

        ResolveInfo resolveInfo =
            activity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String browserPackageName = null;
        if (resolveInfo != null && resolveInfo.activityInfo != null) {
          browserPackageName = resolveInfo.activityInfo.packageName;
          Log.d(TAG, "Using browser in package " + browserPackageName);
        }

        // Needed for default Android browser / Chrome only apparently
        if ("com.android.browser".equals(browserPackageName) || "com.android.chrome".equals(browserPackageName)) {
          intent.setPackage(browserPackageName);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.putExtra(Browser.EXTRA_APPLICATION_ID, browserPackageName);
        }

        try {
          activity.startActivity(intent);
        } catch (ActivityNotFoundException ignored) {
          Log.w(TAG, "Can't find anything to handle VIEW of URI " + url);
        }
        break;
    }
  }

  public void quitSynchronously() {
    state = State.DONE;
    cameraManager.stopPreview();
    Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
    quit.sendToTarget();
    try {
      // Wait at most half a second; should be enough time, and onPause() will timeout quickly
      decodeThread.join(500L);
    } catch (InterruptedException e) {
      // continue
    }

    // Be absolutely sure we don't send any queued up messages
    removeMessages(R.id.decode_succeeded);
    removeMessages(R.id.decode_failed);
  }

  private void restartPreviewAndDecode() {
    if (state == State.SUCCESS) {
      state = State.PREVIEW;
//      cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.ocr_continuous_decode);
      activity.drawViewfinder();
    }
  }


  /**
   *  Send a decode request for realtime OCR mode
   */
  private void restartOcrPreviewAndDecode() {
    // Continue capturing camera frames
    cameraManager.startPreview();
    Log.e(TAG, " restartOcrPreviewAndDecod++++++++++++++++++++");
    // Continue requesting decode of images
    cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.ocr_continuous_decode);
    activity.drawViewfinder();
  }


  void resetState() {
    //Log.d(TAG, "in restart()");
    if (state == State.CONTINUOUS_PAUSED) {
      Log.d(TAG, "Setting state to CONTINUOUS");
      state = State.CONTINUOUS;
      restartPreviewAndDecode();
    }
  }
  void stop() {
    // TODO See if this should be done by sending a quit message to decodeHandler as is done
    // below in quitSynchronously().

    Log.d(TAG, "Setting state to CONTINUOUS_PAUSED.");
    state = State.CONTINUOUS_PAUSED;
    removeMessages(R.id.ocr_continuous_decode);
    removeMessages(R.id.ocr_decode);
    removeMessages(R.id.ocr_continuous_decode_failed);
    removeMessages(R.id.ocr_continuous_decode_succeeded); // TODO are these removeMessages() calls doing anything?

    // Freeze the view displayed to the user.
//    CameraManager.get().stopPreview();
  }

}
