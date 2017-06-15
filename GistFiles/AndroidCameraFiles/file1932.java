File imageFile; // use this to keep a reference to the file you create so that we can access it from onActivityResult()

private void createImageFile() throws IOException {
    String imageFileName = "image" + System.currentTimeMillis() + "_"; // give it a unique filename
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    // use this if you want android to automatically save it into the device's image gallery:
    // File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    
    imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
}