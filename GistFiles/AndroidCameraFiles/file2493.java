if (resultCode == RESULT_OK) {
  ...
  else if (requestCode == CAMERA_WITH_FILEPATH) {
    // Get the image from the filepath you specified when you
    // started the camera intent.
    String filepath = getOutputLink(TEMP_JPEG_FILENAME);
    Bitmap img = BitmapFactory.decodeFile(filepath);
    mImage.setImageBitmap(img);
    Toast.makeText(this, "Image is saved in: " + filepath, Toast.LENGTH_SHORT).show();
  }
}