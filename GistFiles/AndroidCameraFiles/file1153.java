// VideoChatActivity#onCreate()
// Then we set that view, and pass a Runnable to run once the surface is ready
VideoRendererGui.setView(mVideoView, null);

// Now that VideoRendererGui is ready, we can get our VideoRenderer.
// IN THIS ORDER. Effects which is on top or bottom
remoteRender = VideoRendererGui.create(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
localRender = VideoRendererGui.create(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, true);