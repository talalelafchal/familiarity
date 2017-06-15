	private void invokeImageCropIntent(String inputPicturePath, String outputPicturePath) {

		Intent intent;
		try {
			// Initialize intent
			intent = new Intent("com.android.camera.action.CROP");

			// initialize the Uri for the captures or gallery image
			Uri imageUri = Uri.fromFile(new File(inputPicturePath));

			//set image properties
			intent.setDataAndType(imageUri, "image/*");
			intent.putExtra("outputX", 400);
			intent.putExtra("outputY", 400);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);

			// Here's my attempt to ask the intent to save output data as file
			File f = new File(outputPicturePath);
			//mCurrentThumbPath = f.getAbsolutePath();
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			intent.putExtra("output", Uri.fromFile(f));

			// When changing this to false it worked
			intent.putExtra("return-data", false);

			startActivityForResult(intent, KeyConstants.KEY_IMAGE_CONFIRM.ordinal());
		} catch (ActivityNotFoundException e) {
		  e.printStackTrace();
		  // call your custom intent
			//intent = new Intent(this, ImagePreview.class);
			//intent.putExtra(KeyConstants.KEY_IMAGEURI.name(), inputPicturePath);
			//startActivityForResult(intent, KeyConstants.KEY_IMAGE_CONFIRM.ordinal());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}