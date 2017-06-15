@Override
protected void onResume() {
    super.onResume();
    this.videoView.onResume();        // GLSurfaceView
    this.localVideoSource.restart(); // VideoSource
}