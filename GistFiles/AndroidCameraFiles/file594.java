
    private static final int TAKE_PICTURE_CAM = 1;
	private static final int TAKE_PICTURE_GALLERY = 2;
	private Uri outputFileUri;
           
    //to take picture from camera.

Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
outputFileUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
takePicture.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
startActivityForResult(takePicture, TAKE_PICTURE_CAM);

//to pick photo from gallery

Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
startActivityForResult(pickPhoto , TAKE_PICTURE_GALLERY);

onactivity result code::

protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
switch(requestCode) {
case TAKE_PICTURE_CAM:
    if(resultCode == RESULT_OK){  
        //Uri selectedImage = imageReturnedIntent.getData();	//this is only for no file output use
        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
        //you may wany to resize
        float scale=(float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getWidth();
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*scale), (int)(bitmap.getHeight()*scale), true);
		bitmap.recycle();
        imageview.setImageURI(resizedBitmap);
    }

break; 
case TAKE_PICTURE_GALLERY:
    if(resultCode == RESULT_OK){  
        Uri selectedImage = imageReturnedIntent.getData();
        imageview.setImageURI(selectedImage);
    }
break;
}
}

	protected void onSaveInstanceState(Bundle outState) {

	super.onSaveInstanceState(outState);
	outState.putParcelable("outputFileUri", outputFileUri);

	}

	 

	@Override

	protected void onRestoreInstanceState(Bundle savedInstanceState) {

	super.onRestoreInstanceState(savedInstanceState);
	outputFileUri = savedInstanceState.getParcelable("outputFileUri");

	}
	
//finally add these permisions in manifeast file

 <uses-permission android:name="android.permission.CAMERA" />
 <uses-feature android:name="android.hardware.camera" />
 <uses-feature android:name="android.hardware.camera.autofocus" />
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />