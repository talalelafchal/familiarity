@Override
public void pictureTaken(String filename){
  GGMainActivity.listOfFiles.add(filename);
  if (count >= 5){
    Log.d(TAG, "taken 5");
    // use the interface to do a fragment transaction in the activity
    flowControl.startBuild();
    return;
  }
  if (!isAlive)
    return;

  camera.startPreview();
  takePicture();
}