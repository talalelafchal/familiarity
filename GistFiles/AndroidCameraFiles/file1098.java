 private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items,     new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    try {


                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, Tags.REQUEST_CAMERA);
                    }catch(ActivityNotFoundException anfe){
                        //display an error message
                        String errorMessage = "Whoops - your device doesn't support capturing images!";
                        Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else if (items[item].equals("Choose from Library")) {
                    try {


                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                Tags.SELECT_FILE);
                    }
                    catch(ActivityNotFoundException ab){
                        //display an error message
                        String errorMessage = "Whoops - your device doesn't support pick images from Gallery!";
                        Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }//end  of method select image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Tags.SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == Tags.REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if(requestCode == Tags.PIC_CROP) {
                Bundle bundle =data.getExtras();
                setImage((Bitmap) bundle.getParcelable("data"));
            }

        }
    }
    private void onCaptureImageResult(Intent data) {
        picUri=data.getData();

        if (new Utilities().isBuildVersionLessThan(Build.VERSION_CODES.LOLLIPOP))
            sendToCrop();
        else setImage((Bitmap)data.getExtras().get("data"));


    }
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);


        picUri=data.getData();
       if (new Utilities().isBuildVersionLessThan(Build.VERSION_CODES.LOLLIPOP))
        sendToCrop();
        else try {
           setImage( MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData()));
       } catch (IOException e) {
           e.printStackTrace();
       }
//

    }
    private void sendToCrop()
    {
        try {
            //call the standard crop action intent (the user device may not support it)

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, Tags.PIC_CROP);
          }
    catch(ActivityNotFoundException ab){
        //display an error message
        String errorMessage = "Whoops - your device doesn't support crop Images!";
        Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
        toast.show();
    }

    }
    private  void setImage(Bitmap bitmap){
  

        imgvwProfile.setImageBitmap(bitmap);
        


    }