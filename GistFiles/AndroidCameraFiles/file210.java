LocalMedia localMedia = LocalMedia.create(context);
VideoConstraints videoConstraints = new VideoConstraints.Builder()
		.minVideoDimensions(new VideoDimensions(320, 180))
		.maxVideoDimensions(new VideoDimensions(640, 360))
		.minFps(5)
		.maxFps(15)
		.build();
CameraCapturer cameraCapturer = new CameraCapturer(this, CameraCapturer.CameraSource.FRONT_CAMERA);
cameraVideoTrack = localMedia.addVideoTrack(true, cameraCapturer, videoConstraints);