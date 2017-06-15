@Override
protected void onPause() {
    super.onPause();
    this.videoView.onPause();     // GLSurfaceView
    this.localVideoSource.stop(); // VideoSource
}