
Camera.Parameters p = mCam.getParameters();
p.setPreviewSize(camHeight, camWidth);
p.setPreviewFpsRange(minFps, maxFps);
mCam.setParameters(p);
texture = new SurfaceTexture(10);
mCam.setPreviewTexture(texture);
mCam.startPreview();
mCam.setPreviewCallback(new PreviewCallback() {
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        NativeAndroidCamera.getFrame(data, camWidth, camHeight,
                pixels.getNativeObjAddr(), pixelsByte,
                pixelsByteRgb);
    }
});