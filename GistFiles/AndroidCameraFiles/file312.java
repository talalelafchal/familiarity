        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File photoFile = null;
				try {
					photoFile = createImageFile();
				} catch (IOException ex) {
				}
				// Continue only if the File was
				// successfully created
				if (photoFile != null) {
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
					startActivityForResult(takePictureIntent, RESULT_LOAD_IMG);
				}
				
				
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		// File storageDir = getFilesDir();

		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);
		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();

		genralHelper.savePreferences("camfile", mCurrentPhotoPath);

		Log.d("DEBUG", "mCurrentPhotoPath " + mCurrentPhotoPath);
		return image;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == RESULT_LOAD_IMG) {
				if (genralHelper.loadPreferences("camfile") != null) {

				}
			}
		}
	}