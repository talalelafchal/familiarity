  //PERMISOS EN Manifest.xml
  <uses-permission android:name="android.permission.CAMERA" />
  <uses-feature android:name="android.hardware.camera" />
  <uses-feature android:name="android.hardware.camera.autofocus" />
    
    
  
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
      //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
  }else{
      final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      //Uri fileUri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
      startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
  }