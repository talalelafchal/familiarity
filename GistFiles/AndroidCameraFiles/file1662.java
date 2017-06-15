            // Surface変更時
            // プレビューのパラメーターを設定し、プレビューを開始する
            public void surfaceChanged(SurfaceHolder holder, int format,    int width, int height) {
                Log.d(TAG, "surfaceChanged width:" + width + " height:" + height);

                Camera.Parameters parameters = camera_.getParameters();

                // 縦画面の場合回転させる
                if ( rootView_.getWidth() < rootView_.getHeight()) {
                    // 縦画面
//                  parameters.setRotation(90);
                    camera_.setDisplayOrientation(90);
                }else{
                    // 横画面
//                  parameters.setRotation(0);
                    camera_.setDisplayOrientation(0);
                }