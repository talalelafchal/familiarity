// via: https://code.google.com/p/android/issues/detail?id=9570

recorder = new MediaRecorder();
recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

// The following line should not be necessary, or it should be possible to create
// a Surface without a foreground UI.
recorder.setPreviewDisplay(mSurface);
      
recorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/testfile");
recorder.prepare();
recorder.start();