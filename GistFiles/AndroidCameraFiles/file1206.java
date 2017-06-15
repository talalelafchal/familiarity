//Add the following to your Activity
//Modify context and onActivityResult() if using Fragments
//Call startImageCapture() to initiate
//File is returned in onActivityResult()

private Uri mImageUri;
private static final int CAMERA_REQUEST = 1888;

public void startImageCapture(){
    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    File photo;
    try {
        // place where to store camera taken picture
        photo = createTemporaryFile("capture", ".jpg");
        photo.delete();
    } catch (Exception e) {
        Log.v("Djsce Image capture", "Can't create file to take picture!");
        Toast.makeText(getApplicationContext(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG).show();
        return;
    }
    mImageUri = Uri.fromFile(photo);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
    startActivityForResult(intent, CAMERA_REQUEST);
}

public File grabImageFile(boolean compress,int quality) {
    File returnFile = null;
    try {
        //InputStream is = getContentResolver().openInputStream(mImageUri);
        returnFile = new File(mImageUri.getPath());
        if(returnFile.exists() && compress){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(returnFile.getAbsolutePath(),bmOptions);
            File compressedFile = createTemporaryFile("capture_compressed", ".jpg");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(compressedFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            returnFile.delete();
            returnFile = compressedFile;
        }
//
    } catch (Exception e){
        Log.e("Image Capture Error",e.getMessage());
    }
    return returnFile;
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
        File f = grabImageFile(true, 80); //true for compression , 80% quality
        if(f!=null){
            Toast.makeText(getApplicationContext(),"File to upload is " + f.getAbsolutePath(),Toast.LENGTH_LONG).show();
            //call image uplaod code here
            //doFileUpload(f, Constants.IMAGE,f.getName());
        }
        else{
            Toast.makeText(getApplicationContext(),"Image Capture Error",Toast.LENGTH_LONG).show();
        }
    }
    super.onActivityResult(requestCode, resultCode, intent);
}

private File createTemporaryFile(String part, String ext) throws Exception {
    File tempDir = Environment.getExternalStorageDirectory();
    tempDir = new File(tempDir.getAbsolutePath() + "/djsce_camera_capture/");
    if (!tempDir.exists()) {
        tempDir.mkdir();
    }
    return File.createTempFile(part, ext, tempDir);
}