/*
 * Copyright (C) 2010 ZXing authors
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

package com.ctrlsmart.captureTool;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ctrlsmart.fpcx.CaptureActivity;
import com.ctrlsmart.fpcx.R;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

//import com.google.zxing.BinaryBitmap;
//import com.google.zxing.DecodeHintType;
//import com.google.zxing.MultiFormatReader;
//import com.google.zxing.ReaderException;
//import com.google.zxing.Result;
//import com.google.zxing.common.HybridBinarizer;

public final class DecodeHandler extends Handler {
  private final TessBaseAPI baseApi;
  private static final String TAG = DecodeHandler.class.getSimpleName();
  private long timeRequired;
  private final CaptureActivity activity;
//  private final MultiFormatReader multiFormatReader;
  private boolean running = true;
  private static boolean isDecodePending;
  private Bitmap bitmap;
  DecodeHandler(CaptureActivity activity) {
//    multiFormatReader = new MultiFormatReader();
//    multiFormatReader.setHints(hints);
    this.activity = activity;
    baseApi = activity.getBaseApi();
  }

  @Override
  public void handleMessage(Message message) {
    if (!running) {
      return;
    }
    switch (message.what) {
      case R.id.ocr_continuous_decode:
        Log.e("DecodeHandler","handleMessage ,R.id.ocr_continuous_decode");
        // Only request a decode if a request is not already pending.
        if (!isDecodePending) {
          isDecodePending = true;
          Log.e("DecodeHandler","handleMessage ,R.id.ocr_continuous_decode22222");
          ocrContinuousDecode((byte[]) message.obj, message.arg1, message.arg2);
        }
        break;
      case R.id.decode:
        ocrDecode((byte[]) message.obj, message.arg1, message.arg2);
        break;
      case R.id.quit:
        running = false;
        Looper.myLooper().quit();
        break;
    }
  }
  public static void resetDecodeState() {
    isDecodePending = false;
  }

  /**
   *  Perform an OCR decode for realtime recognition mode.
   *
   * @param data Image data
   * @param width Image width
   * @param height Image height
   */
  private void ocrContinuousDecode(byte[] data, int width, int height) {
    PlanarYUVLuminanceSource source = activity.getCameraManager().buildLuminanceSource(data, width, height);
    Log.e("DecodeHandler","source");Log.e("DecodeHandler","handleMessage ,R.id.ocr_continuous_decode");

    if (source == null) {
      sendContinuousOcrFailMessage();
      return;
    }
    bitmap = source.renderCroppedGreyscaleBitmap();

    OcrResult ocrResult = getOcrResult();
    Handler handler = activity.getHandler();
    if (handler == null) {
      return;
    }

    if (ocrResult == null) {
      try {
        sendContinuousOcrFailMessage();
      } catch (NullPointerException e) {
        activity.stopHandler();
      } finally {
        bitmap.recycle();
        baseApi.clear();
      }
      return;
    }

    try {
      Message message = Message.obtain(handler, R.id.ocr_continuous_decode_succeeded, ocrResult);
      message.sendToTarget();
    } catch (NullPointerException e) {
      activity.stopHandler();
    } finally {
      baseApi.clear();
    }
  }


  private OcrResult getOcrResult() {
    OcrResult ocrResult;
    String textResult;
    long start = System.currentTimeMillis();

    try {
      baseApi.setImage(ReadFile.readBitmap(bitmap));
      textResult = baseApi.getUTF8Text();
      timeRequired = System.currentTimeMillis() - start;

      // Check for failure to recognize text
      if (textResult == null || textResult.equals("")) {
        return null;
      }
      ocrResult = new OcrResult();
      ocrResult.setWordConfidences(baseApi.wordConfidences());
      ocrResult.setMeanConfidence( baseApi.meanConfidence());
      if (ViewfinderView.DRAW_REGION_BOXES) {
        ocrResult.setRegionBoundingBoxes(baseApi.getRegions().getBoxRects());
      }
      if (ViewfinderView.DRAW_TEXTLINE_BOXES) {
        ocrResult.setTextlineBoundingBoxes(baseApi.getTextlines().getBoxRects());
      }
      if (ViewfinderView.DRAW_STRIP_BOXES) {
        ocrResult.setStripBoundingBoxes(baseApi.getStrips().getBoxRects());
      }

      // Always get the word bounding boxes--we want it for annotating the bitmap after the user
      // presses the shutter button, in addition to maybe wanting to draw boxes/words during the
      // continuous mode recognition.
      ocrResult.setWordBoundingBoxes(baseApi.getWords().getBoxRects());

//      if (ViewfinderView.DRAW_CHARACTER_BOXES || ViewfinderView.DRAW_CHARACTER_TEXT) {
//        ocrResult.setCharacterBoundingBoxes(baseApi.getCharacters().getBoxRects());
//      }
    } catch (RuntimeException e) {
      Log.e("OcrRecognizeAsyncTask", "Caught RuntimeException in request to Tesseract. Setting state to CONTINUOUS_STOPPED.");
      e.printStackTrace();
      try {
        baseApi.clear();
        activity.stopHandler();
      } catch (NullPointerException e1) {
        // Continue
      }
      return null;
    }
    timeRequired = System.currentTimeMillis() - start;
    ocrResult.setBitmap(bitmap);
    ocrResult.setText(textResult);
    ocrResult.setRecognitionTimeRequired(timeRequired);
    return ocrResult;
  }



  private void sendContinuousOcrFailMessage() {
    Handler handler = activity.getHandler();
    if (handler != null) {
      Message message = Message.obtain(handler, R.id.ocr_continuous_decode_failed, new OcrResultFailure(timeRequired));
      message.sendToTarget();
    }
  }
  /**
   *  Launch an AsyncTask to perform an OCR decode for single-shot mode.
   *
   * @param data Image data
   * @param width Image width
   * @param height Image height
   */
  private void ocrDecode(byte[] data, int width, int height) {
//    beepManager.playBeepSoundAndVibrate();
//    activity.displayProgressDialog();

    // Launch OCR asynchronously, so we get the dialog box displayed immediately
    new OcrRecognizeAsyncTask(activity, baseApi, data, width, height).execute();
  }
//  /**
//   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
//   * reuse the same reader objects from one decode to the next.
//   *
//   * @param data   The YUV preview frame.
//   * @param width  The width of the preview frame.
//   * @param height The height of the preview frame.
//   */
//  private void decode(byte[] data, int width, int height) {
//    long start = System.currentTimeMillis();
//    Result rawResult = null;
//    PlanarYUVLuminanceSource source = activity.getCameraManager().buildLuminanceSource(data, width, height);
//    if (source != null) {
//      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//      try {
//        rawResult = multiFormatReader.decodeWithState(bitmap);
//      } catch (ReaderException re) {
//        // continue
//      } finally {
//        multiFormatReader.reset();
//      }
//    }
//
//    Handler handler = activity.getHandler();
//    if (rawResult != null) {
//      // Don't log the barcode contents for security.
//      long end = System.currentTimeMillis();
//      Log.d(TAG, "Found barcode in " + (end - start) + " ms");
//      if (handler != null) {
//        Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
//        Bundle bundle = new Bundle();
//        bundleThumbnail(source, bundle);
//        message.setData(bundle);
//        message.sendToTarget();
//      }
//    } else {
//      if (handler != null) {
//        Message message = Message.obtain(handler, R.id.decode_failed);
//        message.sendToTarget();
//      }
//    }
//  }

//  private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
//    int[] pixels = source.renderThumbnail();
//    int width = source.getThumbnailWidth();
//    int height = source.getThumbnailHeight();
//    Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
//    ByteArrayOutputStream out = new ByteArrayOutputStream();
//    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
//    bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
//    bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width / source.getWidth());
//  }

}
