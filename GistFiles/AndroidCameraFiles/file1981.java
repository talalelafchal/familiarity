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

package com.ctrlsmart.captureTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.ctrlsmart.fpcx.R;

import java.util.List;

//import com.google.zxing.ResultPoint;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {
  private OcrResultText resultText;
  /** Flag to draw boxes representing the results from TessBaseAPI::GetRegions(). */
  static final boolean DRAW_REGION_BOXES = false;

  /** Flag to draw boxes representing the results from TessBaseAPI::GetTextlines(). */
  static final boolean DRAW_TEXTLINE_BOXES = true;

  /** Flag to draw boxes representing the results from TessBaseAPI::GetStrips(). */
  static final boolean DRAW_STRIP_BOXES = false;
  static final boolean DRAW_WORD_BOXES = true;
  private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
  private static final long ANIMATION_DELAY = 80L;
  private static final int CURRENT_POINT_OPACITY = 0xA0;
  private static final int MAX_RESULT_POINTS = 20;
  private static final int POINT_SIZE = 6;

  private CameraManager cameraManager;
  private final Paint paint;
  private Bitmap resultBitmap;
  private final int maskColor;
  private final int resultColor;
  private final int laserColor;
  private final int resultPointColor;
  private final int conerColor;
  private int scannerAlpha;
  private List<Rect> wordBoundingBoxes;
//  private List<ResultPoint> possibleResultPoints;
//  private List<ResultPoint> lastPossibleResultPoints;

  // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Initialize these once for performance rather than calling them every time in onDraw().
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Resources resources = getResources();
    maskColor = resources.getColor(R.color.viewfinder_mask);
    resultColor = resources.getColor(R.color.result_view);
    laserColor = resources.getColor(R.color.viewfinder_laser);
    resultPointColor = resources.getColor(R.color.possible_result_points);
    conerColor = resources.getColor(R.color.result_points);
    scannerAlpha = 0;
//    possibleResultPoints = new ArrayList<>(5);
//    lastPossibleResultPoints = null;
  }

  public void setCameraManager(CameraManager cameraManager) {
    this.cameraManager = cameraManager;
  }

  @SuppressLint("DrawAllocation")
  @Override
  public void onDraw(Canvas canvas) {
    if (cameraManager == null) {
      return; // not ready yet, early draw before done configuring
    }
    Rect frame = cameraManager.getFramingRect();
    Rect previewFrame = cameraManager.getFramingRectInPreview();
    if (frame == null || previewFrame == null) {
      return;
    }
    int width = canvas.getWidth();
    int height = canvas.getHeight();

    // Draw the exterior (i.e. outside the framing rect) darkened
    paint.setColor(resultBitmap != null ? resultColor : maskColor);
    canvas.drawRect(0, 0, width, frame.top, paint);
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

    if (resultText != null) {

      // Only draw text/bounding boxes on viewfinder if it hasn't been resized since the OCR was requested.
      Point bitmapSize = resultText.getBitmapDimensions();
      previewFrame = cameraManager.getFramingRectInPreview();
      if (bitmapSize.x == previewFrame.width() && bitmapSize.y == previewFrame.height()) {


        float scaleX = frame.width() / (float) previewFrame.width();
        float scaleY = frame.height() / (float) previewFrame.height();

        if (DRAW_WORD_BOXES) {
          wordBoundingBoxes = resultText.getWordBoundingBoxes();
          paint.setAlpha(0xFF);
          paint.setColor(0xFF00CCFF);
          paint.setStyle(Style.STROKE);
          paint.setStrokeWidth(1);
          for (int i = 0; i < wordBoundingBoxes.size(); i++) {
            // Draw a bounding box around the word
            Rect rect = wordBoundingBoxes.get(i);
            canvas.drawRect(
                    frame.left + rect.left * scaleX,
                    frame.top + rect.top * scaleY,
                    frame.left + rect.right * scaleX,
                    frame.top + rect.bottom * scaleY, paint);
          }
        }

      }
    }
    paint.setAlpha(0);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(conerColor);
    canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
    canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
    canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
    canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

    // Draw the framing rect corner UI elements
    paint.setColor(resultPointColor);
    canvas.drawRect(frame.left - 10, frame.top - 10, frame.left + 10, frame.top, paint);
    canvas.drawRect(frame.left - 10, frame.top, frame.left, frame.top + 10, paint);
    canvas.drawRect(frame.right - 10, frame.top - 10, frame.right + 10, frame.top, paint);
    canvas.drawRect(frame.right, frame.top - 10, frame.right + 10, frame.top + 10, paint);
    canvas.drawRect(frame.left - 10, frame.bottom, frame.left + 10, frame.bottom + 10, paint);
    canvas.drawRect(frame.left - 10, frame.bottom - 10, frame.left, frame.bottom, paint);
    canvas.drawRect(frame.right - 10, frame.bottom, frame.right + 10, frame.bottom + 10, paint);
    canvas.drawRect(frame.right, frame.bottom - 10, frame.right + 10, frame.bottom + 10, paint);

  }

  public void drawViewfinder() {
//    Bitmap resultBitmap = this.resultBitmap;
//    this.resultBitmap = null;
//    if (resultBitmap != null) {
//      resultBitmap.recycle();
//    }
    invalidate();
  }

  /**
   * Draw a bitmap with the result points highlighted instead of the live scanning display.
   *
   * @param barcode An image of the decoded barcode.
   */
  public void drawResultBitmap(Bitmap barcode) {
    resultBitmap = barcode;
    invalidate();
  }

//  public void addPossibleResultPoint(ResultPoint point) {
//    List<ResultPoint> points = possibleResultPoints;
//    synchronized (points) {
//      points.add(point);
//      int size = points.size();
//      if (size > MAX_RESULT_POINTS) {
//        // trim it
//        points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
//      }
//    }
//  }
public void addResultText(OcrResultText text) {
  resultText = text;
}

  /**
   * Nullifies OCR text to remove it at the next onDraw() drawing.
   */
  public void removeResultText() {
    resultText = null;
  }

//  public void removeResultText() {
////    resultText = null;
//  }
}
