public void startCameraIntent() {
  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

  if (intent.resolveActivity(getPackageManager()) != null) {
      try {
          createImageFile();
      } catch (IOException e) {
          e.printStackTrace();
      }

      if (imageFile != null) {
          Uri imageUri = FileProvider.getUriForFile(MainActivity.this,
                  BuildConfig.APPLICATION_ID + ".fileprovider", imageFile);
          intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
          } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
              ClipData clip = ClipData.newUri(getContentResolver(), "A photo", imageUri);

              intent.setClipData(clip);
              intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
          } else {
              List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(
                      intent, PackageManager.MATCH_DEFAULT_ONLY);
              for (ResolveInfo info : resInfoList) {
                  String packageName = info.activityInfo.packageName;
                  grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
              }
          }
      }
      startActivityForResult(intent, REQUEST_CAMERA);
  } else {
      // 设备没有相机
  }
}

private void createImageFile() throws IOException {
    String imageFileName = "image" + System.currentTimeMillis() + "_"; // 保证图片名独一无二
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

//        如果你想直接保存到设备的图片库里，就这样写：
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
        if (requestCode == REQUEST_CAMERA) {
          // 获取拍照结果
        }
    }
}