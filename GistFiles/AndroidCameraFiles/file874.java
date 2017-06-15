
        // プレビューコールバック
        private final Camera.PreviewCallback previewCallback_ =    new Camera.PreviewCallback() {

            public void onPreviewFrame(byte[] data, Camera camera) {
                Log.d(TAG, "onPreviewFrame");

                camera_.setPreviewCallback(null);  // プレビューコールバックを解除

                // 画像のデコード

                Bitmap decodeBitmap = null;
                int[] rgb = new int[(PREVIEW_WIDTH * PREVIEW_HEIGHT)];
                try {
                    decodeBitmap = Bitmap.createBitmap(PREVIEW_WIDTH, PREVIEW_HEIGHT,   Bitmap.Config.ARGB_8888);
                    decodeYUV420SP(rgb, data, PREVIEW_WIDTH, PREVIEW_HEIGHT);
                    decodeBitmap.setPixels(rgb, 0, PREVIEW_WIDTH, 0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT);
                } catch (Exception e) {
                }

                if (decodeBitmap != null) {
                    int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                    int degrees = 0; //端末の向き(度換算)
                    switch (rotation) {
                        case Surface.ROTATION_0: degrees = 0; break;
                        case Surface.ROTATION_90: degrees = 90; break;
                        case Surface.ROTATION_180: degrees = 180; break;
                        case Surface.ROTATION_270: degrees = 270; break;
                    }
                    Matrix m = new Matrix(); //Bitmapの回転用Matrix
                    m.setRotate(90-degrees);    // 向きが正しくなるように回転角度を補正

                    Bitmap rotated = Bitmap.createBitmap( decodeBitmap, 0, 0, decodeBitmap.getWidth(), decodeBitmap.getHeight(), m, true);

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+ "/camera_preview.jpg");
                        rotated.compress(CompressFormat.JPEG, 100, fos);
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    decodeBitmap.recycle();
                    rotated.recycle();

                }else{
                    // Bitmapデコード失敗
                    Log.d(TAG, "onPreviewFrame bitmap decode error");
                }

                // 処理完了
                inPregress_ = false;    // 処理中フラグをクリア

            }
        };


        // YUV420フォーマット RAWデコード
        static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
            final int frameSize = width * height;

            for (int j = 0, yp = 0; j < height; j++) {
                int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
                for (int i = 0; i < width; i++, yp++) {
                    int y = (0xff & ((int) yuv420sp[yp])) - 16;
                    if (y < 0) y = 0;
                    if ((i & 1) == 0) {
                        v = (0xff & yuv420sp[uvp++]) - 128;
                        u = (0xff & yuv420sp[uvp++]) - 128;
                    }

                    int y1192 = 1192 * y;
                    int r = (y1192 + 1634 * v);
                    int g = (y1192 - 833 * v - 400 * u);
                    int b = (y1192 + 2066 * u);

                    if (r < 0) r = 0; else if (r > 262143) r = 262143;
                    if (g < 0) g = 0; else if (g > 262143) g = 262143;
                    if (b < 0) b = 0; else if (b > 262143) b = 262143;

                    rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
                }
            }
        }

        // AF完了時のコールバック
        private Camera.AutoFocusCallback autoFocusListener_ = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if ( false ) {
                    // 通常撮影
                    camera.autoFocus(null);
                    camera_.takePicture(shutterListener_, null, pictureListener_);
                }else{
                    // 無音撮影
                    camera_.setPreviewCallback(previewCallback_);
                }

                inPregress_ = true; // 処理中フラグ
            }
        };
