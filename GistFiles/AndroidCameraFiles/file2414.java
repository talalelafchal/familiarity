private static final int TAKE_PICTURE = 1;
private Uri imageUri;

public void takePhoto(View view) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Keep the data from build-in camera path.
    File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
    if (file.exists())
    {
        file.delete();
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
    }
    imageUri = Uri.fromFile(photo);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
    startActivityForResult(intent, TAKE_PICTURE);
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
        // Camera request code.
        case TAKE_PICTURE:
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageUri;
                getContentResolver().notifyChange(selectedImage, null);
                ImageView imageView = (ImageView) findViewById(R.id.ImageView);
                try {
                    Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                Log.e("Camera", e.toString());
            }
            break;
        }
    }
}
