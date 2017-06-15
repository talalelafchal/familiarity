sv = new SurfaceView(this);
sv.setOnClickListener(onClickListener);

private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // オートフォーカス
            if (cam != null) {
                cam.autoFocus(autoFocusCallback);
            }
        }
};

private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // 現在のプレビューをデータに変換
                camera.setOneShotPreviewCallback(previewCallback);
            }
        }
};
