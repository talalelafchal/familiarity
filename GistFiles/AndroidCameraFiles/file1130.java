public void takePhoto(View view) {

        Intent intCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intCamera.putExtra("android.intent.extras.CAMERA_FACING", 1);

        if (intCamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intCamera, Cv.REQUEST_CAMERA);
        }
    }

    public void chooseFromGallery(View view) {

        Intent intGallery = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intGallery, Cv.REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && null != data) {

            Bitmap photo = null;

            switch (requestCode) {

                case Cv.REQUEST_CAMERA:

                    photo = (Bitmap) data.getExtras().get("data");
                    break;

                case Cv.REQUEST_GALLERY:

                    try {
                        // this is craziest line I've ever written yet
                        photo = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),
                                ContentUris.parseId(data.getData()),
                                MediaStore.Images.Thumbnails.MINI_KIND, null);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
            ivAvatar.setImageBitmap(photo);
            etFirstName.requestFocus();
            btnSaveChanges.setEnabled(true);
            pictureFrame.setVisibility(View.GONE);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(Cv.PREFS_PIC_BASE64, bitmapToBase64(photo))
                    .apply();
        }
    }