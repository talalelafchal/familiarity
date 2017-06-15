private String cameraFileName;
public static final int CHOICE_AVATAR_FROM_CAMERA = 0;
public static final int CHOICE_AVATAR_FROM_GALLERY = 1;
public static final int CHOICE_AVATAR_FROM_CAMERA_CROP = 3;

public void choiceAvatarFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraFileName = "file" + System.currentTimeMillis() + ".png";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), cameraFileName);
        if(!file.exists()){
            file.mkdirs();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CHOICE_AVATAR_FROM_CAMERA_CROP);
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOICE_AVATAR_FROM_CAMERA || requestCode == CHOICE_AVATAR_FROM_GALLERY) {
                Utils.generateToast(getContext(), "CHOICE_AVATAR_FROM_CAMERA", true);
                Bitmap avatar = getBitmapFromData(data);
                storeImage(avatar);
            } else if (requestCode == CHOICE_AVATAR_FROM_CAMERA_CROP) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                Uri uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), cameraFileName));
                intent.setDataAndType(uri, "image/*");
                startActivityForResult(getCropIntent(intent), CHOICE_AVATAR_FROM_CAMERA);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void storeImage(Bitmap image) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "crop_avatar.png");
        try {
            if(!file.exists())
                file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }
    }