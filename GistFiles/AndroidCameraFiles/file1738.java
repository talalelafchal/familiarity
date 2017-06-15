private void takePictureWithCamera() {
        // create an empty file to store image
        File photoFile = createImageFile();

        // lấy dường dẫn trong điện thoại
        selectedPhotoPath = Uri.parse(photoFile.getAbsolutePath());

        // đặt tả đường dẫn to save image.
        captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        // TAKE_PHOTO_REQUEST_CODE: dùng để xác định intent khi được trả về
        // Verify that the intent will resolve to an activity
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(captureIntent, TAKE_PHOTO_REQUEST_CODE);
        }
    }
    
    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // create a path
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + APP_PICTURE_DIRECTORY);
        storageDir.mkdirs();
        File imageFile = null;

        try {
            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    FILE_SUFFIX_JPG,         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
    }