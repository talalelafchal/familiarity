    //Override in your touch-enabled view (this can be differen than the view you use for displaying the cam preview)
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != MotionEvent.ACTION_DOWN) {
            return false;
        }
        if (mManualFocusEngaged) {
            Log.d(TAG, "Manual focus already engaged");
            return true;
        }

        final Rect sensorArraySize = mCameraInfo.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);

        //TODO: here I just flip x,y, but this needs to correspond with the sensor orientation (via SENSOR_ORIENTATION)
        final int y = (int)((motionEvent.getX() / (float)view.getWidth())  * (float)sensorArraySize.height());
        final int x = (int)((motionEvent.getY() / (float)view.getHeight()) * (float)sensorArraySize.width());
        final int halfTouchWidth  = 150; //(int)motionEvent.getTouchMajor(); //TODO: this doesn't represent actual touch size in pixel. Values range in [3, 10]...
        final int halfTouchHeight = 150; //(int)motionEvent.getTouchMinor();
        MeteringRectangle focusAreaTouch = new MeteringRectangle(Math.max(x - halfTouchWidth,  0),
                                                                 Math.max(y - halfTouchHeight, 0),
                                                                 halfTouchWidth  * 2,
                                                                 halfTouchHeight * 2,
                                                                 MeteringRectangle.METERING_WEIGHT_MAX - 1);

        CameraCaptureSession.CaptureCallback captureCallbackHandler = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                mManualFocusEngaged = false;

                if (request.getTag() == "FOCUS_TAG") {
                    //the focus trigger is complete -
                    //resume repeating (preview surface will get frames), clear AF trigger
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
                    mCameraOps.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                }
            }

            @Override
            public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                super.onCaptureFailed(session, request, failure);
                Log.e(TAG, "Manual AF failure: " + failure);
                mManualFocusEngaged = false;
            }
        };

        //first stop the existing repeating request
        mCameraOps.stopRepeating();

        //cancel any existing AF trigger (repeated touches, etc.)
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
        mCameraOps.capture(mPreviewRequestBuilder.build(), captureCallbackHandler, mBackgroundHandler);

        //Now add a new AF trigger with focus region
        if (isMeteringAreaAFSupported()) {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{focusAreaTouch});
        }
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
        mPreviewRequestBuilder.setTag("FOCUS_TAG"); //we'll capture this later for resuming the preview

        //then we ask for a single request (not repeating!)
        mCameraOps.capture(mPreviewRequestBuilder.build(), captureCallbackHandler, mBackgroundHandler);
        mManualFocusEngaged = true;

        return true;
    }

    private boolean isMeteringAreaAFSupported() {
        return mCameraInfo.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;
    }
