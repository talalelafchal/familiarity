/*
 * Copyright (C) The Android Open Source Project
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
package com.google.android.gms.samples.vision.face.multitracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;

import com.google.android.gms.samples.vision.face.multitracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay mGraphicOverlay;

    public void setBarcodeUpdateListener(OnBarcodeUpdateListener listener) {
        this.mBarcodeUpdateListener = listener;
    }

    private OnBarcodeUpdateListener mBarcodeUpdateListener;

    BarcodeTrackerFactory(GraphicOverlay graphicOverlay) {
        mGraphicOverlay = graphicOverlay;
    }

    public BarcodeTrackerFactory(GraphicOverlay mGraphicOverlay, OnBarcodeUpdateListener mBarcodeUpdateListener) {
        this.mGraphicOverlay = mGraphicOverlay;
        this.mBarcodeUpdateListener = mBarcodeUpdateListener;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay, mBarcodeUpdateListener);
        return new GraphicTracker<>(mGraphicOverlay, graphic);
    }
}

interface OnBarcodeUpdateListener {
    void onBarcodeUpdate(Barcode barcode);
}

/**
 * Graphic instance for rendering barcode position, size, and ID within an associated graphic
 * overlay view.
 */
class BarcodeGraphic extends TrackedGraphic<Barcode> {
    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN
    };
    private static int mCurrentColorIndex = 0;

    private Paint mRectPaint;
    private Paint mTextPaint;
    private volatile Barcode mBarcode;
    private OnBarcodeUpdateListener mBarcodeUpdateListener;

    BarcodeGraphic(GraphicOverlay overlay) {
        super(overlay);
        init();
    }

    public BarcodeGraphic(GraphicOverlay overlay, OnBarcodeUpdateListener barcodeUpdateListener) {
        super(overlay);
        this.mBarcodeUpdateListener = barcodeUpdateListener;
        init();
    }

    void init() {
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mRectPaint = new Paint();
        mRectPaint.setColor(selectedColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(4.0f);

        mTextPaint = new Paint();
        mTextPaint.setColor(selectedColor);
        mTextPaint.setTextSize(36.0f);
    }

    /**
     * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateItem(Barcode barcode) {
        mBarcode = barcode;
        postInvalidate();
        if (mBarcodeUpdateListener != null) {
            new UiOperation().execute(barcode);
        }
    }

    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;
        if (barcode == null) {
            return;
        }

        // Draws the bounding box around the barcode.
        RectF rect = new RectF(barcode.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, mRectPaint);

        // Draws a label at the bottom of the barcode indicate the barcode value that was detected.
        canvas.drawText(barcode.rawValue, rect.left, rect.bottom, mTextPaint);
    }
    /**
     * Calls back the listener on th UI thread
     */
    private class UiOperation extends AsyncTask<Barcode, Void, Barcode> {
        @Override
        protected Barcode doInBackground(Barcode... barcodes) {
            return barcodes[0];
        }

        @Override
        protected void onPostExecute(Barcode barcode) {
            mBarcodeUpdateListener.onBarcodeUpdate(barcode);
        }

    }
}
