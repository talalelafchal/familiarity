// Create a new File object with specified filepath, where the
// captured image will be located.
File file = new File(getOutputLink(TEMP_JPEG_FILENAME));
Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
startActivityForResult(cameraIntent, CAMERA_WITH_FILEPATH);