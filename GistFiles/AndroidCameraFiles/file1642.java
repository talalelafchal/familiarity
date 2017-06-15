/**
     * displays image always in 90 degree
     */
    public static String setRotation(int lastOrientation, int cam) {

        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cam, info);
        int rotate_exif = ExifInterface.ORIENTATION_NORMAL;

        Log.d("cam manager", String.valueOf(info.orientation));


            switch (lastOrientation) {

                case 3:
                    if (cam == 0) {
                        if(info.orientation == 90) {
                            Log.d(TAG, "rotated 180");
                            rotate_exif = ExifInterface.ORIENTATION_ROTATE_180;
                            break;
                        }else if(info.orientation == 270){
                            Log.d(TAG, "no rotation");
                            break;
                        }
                    } else { //Front facing camera cam = 1
                        if(info.orientation == 90) {
                            Log.d(TAG, "no rotation");
                            break;
                        }else if (info.orientation == 270){
                            Log.d(TAG, "rotated 180");
                            rotate_exif = ExifInterface.ORIENTATION_ROTATE_180;
                            break;
                        }
                    }


                case 2:
                    if (cam == 0) {
                        if(info.orientation == 90) {
                            Log.d(TAG, "no rotation");
                            break;
                        }else if(info.orientation == 270){
                            Log.d(TAG, "rotated 180");
                            rotate_exif = ExifInterface.ORIENTATION_ROTATE_180;
                            break;
                        }
                    } else {  //front facing camera cam = 1
                        if (info.orientation == 90) {
                            Log.d(TAG, "rotated 180");
                            rotate_exif = ExifInterface.ORIENTATION_ROTATE_180;
                            break;
                        }else if (info.orientation == 270){
                            Log.d(TAG, "no rotation");
                            break;
                        }
                    }

                case 1:
                    if (cam == 0) {
                        if(info.orientation == 90) {
                            Log.d(TAG, "rotated 90");
                            rotate_exif = ExifInterface.ORIENTATION_ROTATE_90;
                            break;
                        }else if(info.orientation == 270){
                            Log.d(TAG, "rotated 270");
                            rotate_exif = ExifInterface.ORIENTATION_ROTATE_270;
                            break;
                        }
                    } else { //Front facing camera cam = 1
                        if(info.orientation == 90) {
                            Log.d(TAG, "front face rotated 90");
                            rotate_exif = ExifInterface.ORIENTATION_ROTATE_90;
                            break;
                        }else if(info.orientation == 270){
                            Log.d(TAG, "front rotated 270");
                            rotate_exif = ExifInterface.ORIENTATION_ROTATE_270;
                            break;
                        }
                    }

                default:
                    Log.d(TAG, "default");
                    break;

            }
        return String.valueOf(rotate_exif);
    }
    
private static final int ORIENTATION_PORTRAIT_NORMAL = 1;
private static final int ORIENTATION_LANDSCAPE_NORMAL = 2;
private static final int ORIENTATION_LANDSCAPE_INVERTED = 3;
    
@Override
    public void onResume() {
        super.onResume();
        
        //figuring out the orientation
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new OrientationEventListener(getContext(), SensorManager.SENSOR_DELAY_NORMAL) {

                @Override
                public void onOrientationChanged(int orientation) {

                    // determine our orientation based on sensor response
                    //int lastOrientation = mOrientation;

                    if (orientation >= 315 || orientation < 45) {
                        if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                            mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                        }
                    } else if (orientation > 180 && orientation < 315) {
                        if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                            mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                        }
                    } else { // orientation <135 && orientation > 45
                        if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                            mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                        }
                    }

                    /*if (lastOrientation != mOrientation) {

                    }*/
                }
            };

        }

        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }
    
  
//setting cam value
//mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//this.cam = 0;

//mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
//this.cam = 1;

//setting atribute
//exif.setAttribute(ExifInterface.TAG_ORIENTATION, CameraManager.setRotation(lastOrientation, cam));