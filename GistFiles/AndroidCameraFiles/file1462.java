@Override
public void startDisplay() {
  // https://www.grokkingandroid.com/adding-files-to-androids-media-library-using-the-mediascanner/
  try {
    Intent intent =
        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    intent.setData(Uri.fromFile(new File(gifFile)));
    sendBroadcast(intent);
  } catch (Exception e) {
    e.printStackTrace();
  }

  getFragmentManager().beginTransaction()
      .replace(R.id.container, new GifDisplayFrag())
      .commit();
}
