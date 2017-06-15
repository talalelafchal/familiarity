        // JPEGイメージ生成後に呼ばれるコールバック
        private Camera.PictureCallback pictureListener_ = new Camera.PictureCallback() {
            // データ生成完了
            public void onPictureTaken(byte[] data, Camera camera) {
                // SDカードにJPEGデータを保存する
                if (data != null) {

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
                    Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap rotated = Bitmap.createBitmap( original, 0, 0, original.getWidth(), original.getHeight(), m, true);

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+ "/camera_test.jpg");
                        rotated.compress(CompressFormat.JPEG, 100, fos);
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    original.recycle();
                    rotated.recycle();

                    // プレビューを再開する
                    camera.startPreview();
                    inPregress_ = false;    // 処理中フラグをクリア
                }
            }
        };