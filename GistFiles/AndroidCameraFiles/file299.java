// For some help with the Utils class, you may want to take a look 
// at the following android-utils library.
// https://github.com/jaydeepw/android-utils

private Uri mOutputFileUri;


private void openPhotoChooser() {

		// Determine Uri of camera image to save
		final File root = Utils.getStorageDirectory( mContext );
		root.mkdirs();
		final String fname = Long.toString( new Date().getTime() );
		final File sdImageMainDirectory = new File( root, fname );
		mOutputFileUri = Uri.fromFile( sdImageMainDirectory );
	
	    // Camera.
	    final List<Intent> cameraIntents = new ArrayList<Intent>();
	    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	    final PackageManager packageManager = getActivity().getPackageManager();
	    final List<ResolveInfo> listCam = packageManager.queryIntentActivities( captureIntent, 0 );
	    for( ResolveInfo res : listCam ) {
	        final String packageName = res.activityInfo.packageName;
	        final Intent intent = new Intent(captureIntent);
	        intent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name) );
	        intent.setPackage(packageName);
	        intent.putExtra( MediaStore.EXTRA_OUTPUT, mOutputFileUri );
	        cameraIntents.add(intent);
	    }

	    // Filesystem.
	    final Intent galleryIntent = new Intent();
	    galleryIntent.setType("image/*");
	    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

	    // Chooser of filesystem options.
	    final Intent chooserIntent = Intent.createChooser( galleryIntent, "Select Source" );

	    // Add the camera options.
	    chooserIntent.putExtra( Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}) );

	    startActivityForResult( chooserIntent, REQUEST_CAMERA );
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Uri selectedImageUri = null;
		
		Log.v( TAG, "#onActivityResult req: " + requestCode  );
	    if( resultCode == Activity.RESULT_OK ) {
	        if( requestCode == REQUEST_CAMERA ) {
	        	final boolean isCamera;        
	            if( data == null ) {
	                isCamera = true;
	            } else {
	                final String action = data.getAction();
	                if( action == null ) {
	                    isCamera = false;
	                } else {
	                    isCamera = action.equals( android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
	                }
	            }

	            if( isCamera ) {
	                selectedImageUri = mOutputFileUri;
	            } else {
	                selectedImageUri = data == null ? null : data.getData();
	            }
	            
	            // Log.v( TAG, "#onActivityResult selectedImageUri: " + selectedImageUri );
	            
	            
	            if( selectedImageUri != null ) {

			showImageTaken(selectedImageUri);

		    } else {
			// TODO: show no image data received message
		    }
	        }
	    } else {
	    	// user pressed the back button
	    	// and did not action
	    }
	}

   private void showImageTaken (Uri selectedImageUri) {
           Bitmap bmp = Utils.decodeSampledBitmapFromResource( mContext, selectedImageUri, 400, 150 );
           // ImageView.setImageBitmap(bmp);
 
   }