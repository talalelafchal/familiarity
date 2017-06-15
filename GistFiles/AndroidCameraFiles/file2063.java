public void startCameraIntent() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      try {
        imageFile = createImageFile();
      } catch (IOException e) {
        // file wasn't created
      }

      if (imageFile != null) {
        Uri imageUri = FileProvider.getUriForFile(
            ExampleActivity.this,
            BuildConfig.APPLICATION_ID + ".fileprovider", 
            imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        // This is important. Without it, you may get Security Exceptions.
        // Google fails to mention this in their docs...
        // Taken from: https://github.com/commonsguy/cw-omnibus/blob/master/Camera/FileProvider/app/src/main/java/com/commonsware/android/camcon/MainActivity.java
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          ClipData clip = ClipData.newUri(getContentResolver(), "A photo", imageUri);

          intent.setClipData(clip);
          intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        else {
          List<ResolveInfo> resInfoList =
              getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

          for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
          }
        }
      }

      startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }
    else {
      // device doesn't have camera
    }
  }