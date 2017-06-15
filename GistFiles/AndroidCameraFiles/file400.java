    @Bind(R.id.camera_preview)
    CameraPreview mCameraPreview;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inject views
        ButterKnife.bind(this, view);
      
        setupCameraPreview();
    }

    private void setupCameraPreview() {
        mCameraPreview.setCameraReadyListener(new CameraPreview.CameraReadyListener() {

            @Override
            public void onCameraReady(Camera camera) {
                camera.setPreviewCallback(new Camera.PreviewCallback() {

                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        Camera.Parameters parameters = camera.getParameters();
                        Camera.Size size = parameters.getPreviewSize();

                        // Convert uncompressed data to a JPEG
                        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        yuvimage.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, baos);
                        byte[] jpegData = baos.toByteArray();

                        // Create a Bitmap of the JPEG
                        Bitmap frame = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);

                        if (frame != null) {
                          // Do something with frame,e.g. pass to ZXING for barcode analysis.

                        }
                    }
                });

            }

        });
    }