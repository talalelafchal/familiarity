        // 画面タッチ時のコールバック
        OnTouchListener ontouchListener_ = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (camera_ != null && !inPregress_) {
                        // 撮影実行(AF開始)
                        camera_.autoFocus(autoFocusListener_);
                    }
                }
                return false;
            }
        };