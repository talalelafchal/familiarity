if (resultCode == RESULT_OK) {
  if (requestCode == CAMERA_NO_FILEPATH) {
    // Get the image from intent data.
    Bundle bundle = data.getExtras();
    // This Bundle object will contain the Bitmap image, 
    // so no Bitmap decoding will be required.
    Bitmap img = (Bitmap) bundle.get("data");
    mImage.setImageBitmap(img);
  } 
}