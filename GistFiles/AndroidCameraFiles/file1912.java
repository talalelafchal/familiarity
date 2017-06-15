private static int count = 0;
//...

@Override
public void onResume() {
  count = 0;
  //...
}

private void takePicture() {
  Log.d(TAG, "takePicture");
  count++;

  if (!isAlive)
    return;

  camera.takePicture(null, null,
      new PhotoHandler(this));
}

@Override
public void pictureTaken(String filename){
  GGMainActivity.listOfFiles.add(filename);
  if (count >= 5){
    count = 0;
    Log.d(TAG, "taken 5");
    getActivity().finish();
    return;
  }
  if (!isAlive)
    return;

  camera.startPreview();
  takePicture();
}
