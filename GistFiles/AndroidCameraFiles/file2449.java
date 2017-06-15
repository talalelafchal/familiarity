    private void doTakePicture(final int cameraId) throws Exception {
         /*
         Camera class is not thread-safe, and is meant for use from one event thread. 
         Most long-running operations (preview, focus, photo capture, etc) happen asynchronously
         and invoke callbacks as necessary. 
         Callbacks will be invoked on the event thread open(int) was called from.
         This class's methods must never be called from multiple threads at once.
         */
         
        /* Important: mCamera object must be alloc in another Thread, othwerwise, callback will NOT be invoked.
         * Because we wait and block on the Thread that Camera taking Picture callback will be invoked on.
         * so we use "Looper" here to setup a Thread.
         */
        mMonitor.reset();
        new Thread() {
            @Override
            public void run() {
                // Set up a looper to be used by camera.
                Looper.prepare();
                Log.v(TAG, "start loopRun");
                // Save the looper so that we can terminate this thread
                // after we are done with it.
                mLooper = Looper.myLooper();
                mCamera = Camera.open(cameraId);
                mMonitor.signal();  // signal that we have prepared
                Looper.loop();  // Blocks forever until Looper.quit() is called.
                Log.v(TAG, "Looper: quit.");
            }
        }.start();

        mMonitor.waitForSignal(10000); /* must wait for loop run */

        try {
            mCamera.lock(); // before using camera, lock it first

            // preview must be start before take picture
            mCamera.setPreviewDisplay(mActivity.getSurfaceHolder());
            mCamera.startPreview();

            // setting parameters, optional
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(ImageFormat.JPEG);
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            Camera.Size size = sizes.get(0);
            parameters.setPictureSize(size.width, size.height);
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            List<String> supportedFlashModes = parameters.getSupportedFlashModes();
            if (supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                parameters.setFocusMode(Camera.Parameters.FLASH_MODE_ON);
            }
            mCamera.setParameters(parameters);

            // Important: callback when picture is taken
            mMonitor.reset();
            PictureCallback jpegCallback = new PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    try {
                        /* save the picture here */
                        FileOutputStream fos = new FileOutputStream(mOutFile);
                        fos.write(data);
                        fos.close();
                        Log.i(TAG, "notify take picture done");
                        mMonitor.signal();  // notify done
                        /* Note: before jpegCallback is called, should NOT start preview
                         * after calling "takePicture"
                         */
                        mCamera.startPreview(); // preview again
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "File not found: " + e.getMessage());
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception: " + e.getMessage());
                    }
                }
            };


            Thread.sleep(1000); // preview for 1s
            Log.i(TAG, "start taking picture");
            mCamera.takePicture(null, null, jpegCallback);

            // wait at most 4s for picture taken done
            boolean success = mMonitor.waitForSignal(10000); // should wait for taking picture done
            assertTrue(success);

            // keep preview for 1s after take picture
            Thread.sleep(1000);
            mCamera.stopPreview();
        } finally {
            /* clean up */
            if (mCamera != null) {
                mCamera.unlock();
                mCamera.release();
                mCamera = null;
            }

            mLooper.quit();
            mLooper.getThread().join();
        }
    }
