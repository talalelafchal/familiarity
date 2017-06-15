// PICK_PHOTO_CODE is a constant integer
public final static int PICK_PHOTO_CODE = 1046;

// Trigger gallery selection for a photo
public void onPickPhoto(View view)
{
    // Create intent for picking a photo from the gallery
    Intent intent = new Intent(Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    
    // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
    // So as long as the result is not null, it's safe to use the intent.
    if (intent.resolveActivity(getPackageManager()) != null)
    {
       // Bring up gallery to select a photo
       startActivityForResult(intent, PICK_PHOTO_CODE);
    }
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data)
{
    if (data != null)
    {
        Uri photoUri = data.getData();
        // Do something with the photo based on Uri
        Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
        // Load the selected image into a preview
        ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
        ivPreview.setImageBitmap(selectedImage);   
    }
}
