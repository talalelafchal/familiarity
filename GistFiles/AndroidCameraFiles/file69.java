// PICK_PHOTO_CODE is a constant integer
public final static int PICK_PHOTO_CODE = 1046;

// Trigger gallery selection for a photo
public void onPickPhoto(View view)
{
    // Create intent for picking a photo from the gallery
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    
    // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
    // So as long as the result is not null, it's safe to use the intent.
    if (intent.resolveActivity(getPackageManager()) != null)
    {
       // Bring up gallery to select a photo
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PHOTO_CODE);
    }
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data)
{
    if (PICK_PHOTO_CODE == resultCode && null != data)
    {
        if (null != data.getClipData())
            ClipData mClipData = data.getClipData();
             mArrayUri = new ArrayList<Uri>();
             mBitmapsSelected = new ArrayList<Bitmap>();
             for (int i = 0; i < mClipData.getItemCount(); i++)
             {
                 ClipData.Item item = mClipData.getItemAt(i);
                 mArrayUri.add(item.getUri());
                 // !! You may need to resize the image if it's too large
                 Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                 mBitmapsSelected.add(bitmap);
             }
        }
    }
}
