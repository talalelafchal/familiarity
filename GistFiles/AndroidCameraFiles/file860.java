    private String cameraFileName;
    @Override
    public void choiceAvatarFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraFileName = Constants.DOWNLOAD_IMAGE_PATH + System.currentTimeMillis();
        File file = new File(Constants.DOWNLOAD_IMAGE_PATH);
        if(!file.exists()){
            file.mkdirs();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cameraFileName)));
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CHOICE_AVATAR_FROM_CAMERA_CROP);
    }

    @Override
    public void choiceAvatarFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(getCropIntent(intent), CHOICE_AVATAR_FROM_GALLERY);
    }

    private Intent getCropIntent(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOICE_AVATAR_FROM_CAMERA || requestCode == CHOICE_AVATAR_FROM_GALLERY) {
                ToastUtils.toastType0(mActivity, "CHOICE_AVATAR_FROM_CAMERA", Toast.LENGTH_SHORT);
                Bitmap avatar = getBitmapFromData(data);
                // this bitmap is the finish image
            } else if (requestCode == CHOICE_AVATAR_FROM_CAMERA_CROP) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                Uri uri = Uri.fromFile(new File(cameraFileName));
                intent.setDataAndType(uri, "image/*");
                startActivityForResult(getCropIntent(intent), CHOICE_AVATAR_FROM_CAMERA);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Use for decoding camera response data.
     *
     * @param data
     * @return
     */
    public static Bitmap getBitmapFromData(Intent data) {
        Bitmap photo = null;
        Uri photoUri = data.getData();
        if (photoUri != null) {
            photo = BitmapFactory.decodeFile(photoUri.getPath());
        }
        if (photo == null) {
            Bundle extra = data.getExtras();
            if (extra != null) {
                photo = (Bitmap) extra.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }
        }

        return photo;
    }