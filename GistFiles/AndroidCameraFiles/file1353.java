    public class ObtainImage {
    
    private Uri imgUri; 
    
    /** Here goes your code where you handle the way the user chooses between getting from gallery or opening camera **/
    
    /**
     * Opens gallery to choose image. 
     */
    private void chooseFromGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);
    }

    /**
     * Creates empty image file. Launches camera app to capture image.
     */
    private void captureImage() {
        File image = createImageFile();
        imgUri = Uri.fromFile(image);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Creates empty image file in external storage
     *
     * @return File
     */
    private File createImageFile() {
        // External location
        //TODO: Ver si cambiar a almacenamiento interno o externo no publico
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                AppConfig.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + AppConfig.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }

    /**
     * Receiving activity result method will be called after closing the camera or choosing image from gallery
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                /** If the image comes from camera **/
            }
        } else if (requestCode == SELECT_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                imgUri = data.getData();
                /** If the image comes from gallery **/
            }
        }
    }
  }