  
  protected void launchCamera() {
    
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    // photoUri = file:///sdcard/lc-photos/IMG_20130111_125131.jpg
    
    // Shows up as being created in DDMS
    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); 

    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    
  }  
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    
      Log.d(App.TAG , "requestCode " + requestCode);      // 100
	    Log.d(App.TAG , "RESULT_OK " + RESULT_OK);          // -1
	    Log.d(App.TAG , "data.getData()" + data.getData()); // null ????
	    Log.d(App.TAG , "file " + photoUri);                // file:///sdcard/lc-photos/IMG_20130111_125131.jpg

	    
		  if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
	        } 
	    }
	}

/*
01-11 12:54:01.246: E/AndroidRuntime(354): Uncaught handler: thread main exiting due to uncaught exception
01-11 12:54:01.265: E/AndroidRuntime(354): java.lang.RuntimeException: Failure delivering result 
  ResultInfo{who=null, request=100, result=-1, data=null} to 
  activity {com.lettingcheck.activities/com.lettingcheck.activities.PropertiesEdit}: java.lang.NullPointerException
*/