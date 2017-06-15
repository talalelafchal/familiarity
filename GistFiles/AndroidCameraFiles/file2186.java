@Override
public void onAddRemoteStream(final MediaStream remoteStream, final PnPeer peer) {
    VideoChatActivity.this.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            Toast.makeText(VideoChatActivity.this,"Connected to " + peer.getId(), Toast.LENGTH_SHORT).show();
            try {
                if(remoteStream.audioTracks.size()==0 || remoteStream.videoTracks.size()==0) return;
                remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(remoteRender));
                VideoRendererGui.update(remoteRender, 0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
                VideoRendererGui.update(localRender, 72, 72, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, true);
            }
            catch (Exception e){ e.printStackTrace(); }
        }
    });
}