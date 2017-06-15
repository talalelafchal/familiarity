private class DemoRTCListener extends PnRTCListener {
    @Override
    public void onLocalStream(final MediaStream localStream) {
        VideoChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
            }
        });
    }

    @Override
    public void onAddRemoteStream(final MediaStream remoteStream, final PnPeer peer) {
        // Handle remote stream added
    }

    @Override
    public void onMessage(PnPeer peer, Object message) {
        /// Handle Message
    }

    @Override
    public void onPeerConnectionClosed(PnPeer peer) {
        // Quit back to MainActivity
        Intent intent = new Intent(VideoChatActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}