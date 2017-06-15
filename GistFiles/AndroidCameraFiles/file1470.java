@Override
public void onLocalStream(final MediaStream localStream) {
    VideoChatActivity.this.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            if(localStream.videoTracks.size()==0) return;
            localStream.videoTracks.get(0).addRenderer(new VideoRenderer(localRender));
        }
    });
}