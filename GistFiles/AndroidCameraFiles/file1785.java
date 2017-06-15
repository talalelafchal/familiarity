        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
            // View作成
            rootView_ = inflater.inflate(R.layout.fragment_main, container, false);

            // View内のView取得
            surfaceView_ = (SurfaceView) rootView_  .findViewById(R.id.surface_view);

            // SurfaceHolder設定
            SurfaceHolder holder = surfaceView_.getHolder();
            holder.addCallback(surfaceListener_);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            // タッチリスナー設定
            rootView_.setOnTouchListener(ontouchListener_);

            // 画面縦横比設定のためViewTreeObserverにリスナー設定
            rootView_.getViewTreeObserver().addOnGlobalLayoutListener(
                    new OnGlobalLayoutListener() {
                        // レイアウト完了時
                        @Override
                        public void onGlobalLayout() {
                            boolean isLandscape = rootView_.getWidth()>rootView_.getHeight();   // 横画面か?

                            ViewGroup.LayoutParams svlp = surfaceView_.getLayoutParams();
                            if ( isLandscape ) {
                                // 横画面
                                svlp.width = surfaceView_.getHeight() * PREVIEW_WIDTH / PREVIEW_HEIGHT;
                                svlp.height = surfaceView_.getHeight();
                            }else{
                                // 縦画面
                                svlp.width = surfaceView_.getWidth();
                                svlp.height = surfaceView_.getWidth() * PREVIEW_HEIGHT / PREVIEW_WIDTH;
                            }
                            surfaceView_.setLayoutParams(svlp);
                        }
                    });

            return rootView_;
        }