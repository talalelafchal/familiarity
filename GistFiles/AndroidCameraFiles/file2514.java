        // AF完了時のコールバック
        private Camera.AutoFocusCallback autoFocusListener_ = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                inPregress_ = true; // 処理中フラグ
                camera.autoFocus(null);
                camera_.takePicture(shutterListener_, null, pictureListener_);
            }
        };