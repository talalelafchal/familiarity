@Override
protected void onPause() {
    super.onPause();
    this.mVideoView.onPause();
    this.localVideoSource.stop();
}

@Override
protected void onResume() {
    super.onResume();
    this.mVideoView.onResume();
    this.localVideoSource.restart();
}

@Override
protected void onDestroy() {
    super.onDestroy();
    if (this.localVideoSource != null) {
        this.localVideoSource.stop();
    }
    if (this.pnRTCClient != null) {
        this.pnRTCClient.onDestroy();
    }
}