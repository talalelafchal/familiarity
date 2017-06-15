    private void recordVideoUsingCamera(
            Camera camera, String fileName, int durMs, boolean timelapse) throws Exception {
        // FIXME:
        // We should add some test case to use Camera.Parameters.getPreviewFpsRange()
        // to get the supported video frame rate range.
        Camera.Parameters params = camera.getParameters();
        int frameRate = params.getPreviewFrameRate();

        camera.unlock();
        mMediaRecorder.setCamera(camera);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mMediaRecorder.setVideoFrameRate(frameRate);
        mMediaRecorder.setVideoSize(VIDEO_WIDTH, VIDEO_HEIGHT);
        mMediaRecorder.setPreviewDisplay(mActivity.getSurfaceHolder().getSurface());  // set preview
        mMediaRecorder.setOutputFile(fileName);
        mMediaRecorder.setLocation(LATITUDE, LONGITUDE);
        final double captureRate = VIDEO_TIMELAPSE_CAPTURE_RATE_FPS;
        if (timelapse) {
            mMediaRecorder.setCaptureRate(captureRate);
        }

        mMediaRecorder.prepare();
        mMediaRecorder.start();
        Thread.sleep(durMs);
        mMediaRecorder.stop();
        assertTrue(mOutFile.exists());

        assertFalse(mOnErrorCalled);

        int targetDurMs = timelapse? ((int) (durMs * (captureRate / frameRate))): durMs;
        boolean hasVideo = true;
        boolean hasAudio = timelapse? false: true;
        checkTracksAndDuration(targetDurMs, hasVideo, hasAudio, fileName);
    }

