 private static final int IMAGE_PICK_REQUEST = 12345;



private void displayChoiceDialog() {
        String choiceString[] = new String[] {"Gallery" ,"Camera"};
       AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Select image from");
        dialog.setItems(choiceString,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        if (which ==0) {
                            intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        } else {
                            intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                        startActivityForResult(
                                Intent.createChooser(intent, "Select profile picture"), IMAGE_PICK_REQUEST);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_PICK_REQUEST)
                if(resultCode == RESULT_OK&&data!=null) {
                    Uri selectedImageUri = data.getData();
                 if(selectedImageUri!=null)  // image selected from gallary
                    String imagePath=getRealPathFromURI(this, selectedImageUri);
                  else     // image captured from camera
                   Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                }else {
                   Log.d("==>","Operation canceled!");
                }
    }

 /**
     * get actual path from uri
     * @param context context
     * @param contentUri  uri
     * @return actual path
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  projection, null, null, null);

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }