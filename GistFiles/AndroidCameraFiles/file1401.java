/*
 * Creates a new file path into the standard Android pictures directory.
 */
private String getOutputLink(String filename) {
  String directory = "";

  // Check if storage is mounted.
  if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), 
                                            getResources().getString(R.string.app_name));
    // Create the storage directory if it does not exist.
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        return null;
      }
    }
    directory = mediaStorageDir.getPath() + File.separator + filename;
  }
  return directory;
}