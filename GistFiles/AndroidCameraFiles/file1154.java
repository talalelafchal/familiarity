package com.ctrlsmart.captureTool;

import android.os.Handler;
import android.os.Looper;

import com.ctrlsmart.fpcx.CaptureActivity;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2015/4/11.
 */
public class DecodeThread extends Thread {

        public static final String BARCODE_BITMAP = "barcode_bitmap";
        public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

        private final CaptureActivity activity;
//        private final Map<DecodeHintType,Object> hints;
        private Handler handler;
        private final CountDownLatch handlerInitLatch;

        public DecodeThread(CaptureActivity activity
//                     Collection<BarcodeFormat> decodeFormats,
//                            Map<DecodeHintType,?> baseHints,
//                            String characterSet,
//                            ResultPointCallback resultPointCallback
        ) {

            this.activity = activity;
            handlerInitLatch = new CountDownLatch(1);

//            hints = new EnumMap<>(DecodeHintType.class);
//            if (baseHints != null) {
//                hints.putAll(baseHints);
//            }
//
//            // The prefs can't change while the thread is running, so pick them up once here.
//            if (decodeFormats == null || decodeFormats.isEmpty()) {
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
//                decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
//                if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D_PRODUCT, true)) {
//                    decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
//                }
//                if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D_INDUSTRIAL, true)) {
//                    decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
//                }
//                if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_QR, true)) {
//                    decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
//                }
//                if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_DATA_MATRIX, true)) {
//                    decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
//                }
//                if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_AZTEC, false)) {
//                    decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
//                }
//                if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_PDF417, false)) {
//                    decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
//                }
//            }
//            hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
//
//            if (characterSet != null) {
//                hints.put(DecodeHintType.CHARACTER_SET, characterSet);
//            }
//            hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
//            Log.i("DecodeThread", "Hints: " + hints);
        }

        public Handler getHandler() {
            try {
                handlerInitLatch.await();
            } catch (InterruptedException ie) {
                // continue?
            }
            return handler;
        }

        @Override
        public void run() {
            Looper.prepare();
            handler = new DecodeHandler(activity);
            handlerInitLatch.countDown();
            Looper.loop();
        }



}
