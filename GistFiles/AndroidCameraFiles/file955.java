public static final CAMERA_REQUEST_CODE = 5566;
public static Uri imageUri = null;

// Trigger a build-in camera application.
public onPickPhotoThruCamera()
{
    // Create intent for taking a picture and choose it.
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Set a file path for putting the picture temporally.
    File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
    if (file.exists())
    {
        file.delete();
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
    }
    imageUri = Uri.fromFile(file);
    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
    
    // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
    // So as long as the result is not null, it's safe to use the intent.
    if (intent.resolveActivity(getPackageManager()) != null)
    {
       // Bring up gallery to select a photo
        startActivityForResult(Intent.createChooser(intent, "Take a picture"), CAMERA_REQUEST_CODE);
    }
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data)
{
    Uri selectedImageUri = imageUri;
    getContentResolver().notifyChange(selectedImageUri, null);
    try
    {
        this.getFragmentObject(R.id.fragmentContainer, TantraFragment.class).sendPhotoToServer(selectedImageUri.toString());
        Toast.makeText(this, selectedImageUri.toString(), Toast.LENGTH_LONG).show();
    }
    catch (Exception e)
    {
        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
    }
}
