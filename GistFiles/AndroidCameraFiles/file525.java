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