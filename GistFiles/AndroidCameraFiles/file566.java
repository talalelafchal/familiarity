public static File createImageFile(final Context context) throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

    return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
    );
}

public static File createVideoFile(final Context context) throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fileName = "VIDEO_" + timeStamp + "_";
    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);

    return File.createTempFile(
            fileName,  /* prefix */
            ".mp4",         /* suffix */
            storageDir      /* directory */
    );
}

//------

public void startPhotoCapture() {
    if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = ImageUtils.createImageFile(getContext());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                mAbsolutePath = photoFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        CameraUtils.getUri(getContext(), photoFile));
                getFragment().startActivityForResult(intent, Constants.REQUEST_CODE_CAPTURE);
            }
        }
    }
}

public void startVideoCapture() {
    if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File videoFile = null;
            try {
                videoFile = ImageUtils.createVideoFile(getContext());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (videoFile != null) {
                mAbsolutePath = videoFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, CameraUtils.getUri(getContext(), videoFile));
                getFragment().startActivityForResult(intent, CameraUtils.VIDEO_CAPTURE);
            }

        }
    } else {
        Toast.makeText(getFragment().getContext(),
                getFragment().getString(R.string.error_camera), Toast.LENGTH_LONG).show();
    }
}

// -------

public static File getPhotoThumbFromData(final Context context, final String path) {
    try {
        final Bitmap imageBitmap = TextUtils.isEmpty(path) ?
                null : ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 128, 128);
        if (imageBitmap == null) return null;
        final File imageFile;
        try {
            imageFile = ImageUtils.createImageFile(context);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return imageFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
    } catch (OutOfMemoryError outOfMemoryError) {
        outOfMemoryError.printStackTrace();
    }
    return null;
}

public static Uri getUri(final Context context, final File file) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        return FileProvider.getUriForFile(context,
                context.getString(com.nguyenhoanglam.imagepicker.R.string.shared_file_provider),
                file);

    } else{
        return Uri.fromFile(file);
    }
}

public static File getVideoThumbFromData(final Context context, final String path) {
    try {
        final Bitmap bitmap = TextUtils.isEmpty(path) ?
                null : ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        if (bitmap == null) return null;
        final File videoFile;
        try {
            videoFile = ImageUtils.createVideoFile(context);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (videoFile == null) return null;
        try {
            FileOutputStream fos = new FileOutputStream(videoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return videoFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
    } catch (OutOfMemoryError outOfMemoryError) {
        outOfMemoryError.printStackTrace();
    }
    return null;
}




// @@@--------
case Constants.REQUEST_CODE_CAPTURE:
    if (resultCode == RESULT_OK) {
        if (TextUtils.isEmpty(pathForCameraPhotoTmpFile)) {
            captured.setErrorMessage(context.getResources().getString(R.string.error_camera_no_photo));
            break;
        }
        final File photoThumb = getPhotoThumbFromData(context, pathForCameraPhotoTmpFile);
        if (photoThumb == null) {
            captured.setErrorMessage(context.getResources().getString(R.string.error_camera_no_photo));
            break;
        }
        final File photo = new File(pathForCameraPhotoTmpFile);
        captured.setThumbnailFile(photoThumb);
        captured.setFile(photo);
        captured.setSuccesful(true);
    } else if (resultCode == RESULT_CANCELED) {
        captured.setErrorMessage(context.getResources().getString(R.string.error_camera_cancelled));
    } else {
        captured.setErrorMessage(context.getResources().getString(R.string.error_camera_no_photo));
    }
    break;
case CameraUtils.VIDEO_CAPTURE: {
    if (resultCode == RESULT_OK) {
        captured.setVideo(true);
        if (TextUtils.isEmpty(pathForCameraPhotoTmpFile)) {
            captured.setErrorMessage(context.getResources().getString(R.string.error_camera_video_record_failed));
            break;
        }
        final File videoThumb = getVideoThumbFromData(context, pathForCameraPhotoTmpFile);
        if (videoThumb == null) {
            captured.setErrorMessage(context.getResources().getString(R.string.error_camera_video_record_failed));
            break;
        }
        final File video = new File(pathForCameraPhotoTmpFile);
        captured.setThumbnailFile(videoThumb);
        captured.setFile(video);
        captured.setSuccesful(true);
    } else if (resultCode == RESULT_CANCELED) {
        captured.setErrorMessage(context.getResources().getString(R.string.error_camera_cancelled));
    } else {
        captured.setErrorMessage(context.getResources().getString(R.string.error_camera_video_record_failed));
    }
    break;
}
// @@@--------