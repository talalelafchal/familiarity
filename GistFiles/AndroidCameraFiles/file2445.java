private final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 123
pricate final String writeExternalStorageRationale = "You haven't given us permission to use Storage, please enable the permission to store images.";

@Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode){
      case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
          captureImage();
        else{
          if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            showRequestPermissionRationale(writeExternalStorageRationale, "requestPermissionWriteExternalStorage");
          else
            Toast.makeText(MainActivity.this, writeExternalStorageRationale, Toast.LENGTH_LONG).show();
        }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
    
private void takePhoto() {
  //Make sure we have permission to write to external storage
  int hasWriteExternalStoragePermission = checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
  //if does not have permission to write to external storage
  if (hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED)
    captureImage()
  else 
      requestPermissionWriteToLocalStorage();
}

private void requestPermissionWriteToLocalStorage(){
  ActivityCompat.requestPermissions(MainActivity.this,
    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
}

private void captureImage(){
  //Code initiate camera capture
}

private void showRationaleDialog(String message, String permissionMethodName){
  new AlertDialog.Builder(MainActivity.this)
    .setMessage(message)
    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try{
          java.lang.reflect.Method method = MainActivity.class.getDeclaredMethod(permissionMethodName);
          method.setAccessible(true);
          method.invoke(MainActivity.this);
        }
        catch(SecurityException se){Log.e(tag, "Exception: ", se);}
        catch(NoSuchMethodException nsme){Log.e(tag, "Exception: ", nsme);}
        catch(IllegalAccessException iae){Log.e(tag, "Exception: ", iae);}
        catch(InvocationTargetException ite){Log.e(tag, "Exception: ", ite);}
      }
    })
    .setNegativeButton("Cancel", null)
    .create()
    .show();
}