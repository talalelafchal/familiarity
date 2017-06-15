    public void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("return-data", true);

            File f = createNewFile("CROP_");
            try {
                f.createNewFile();
            } catch (IOException ex) {
            }

            picUri = Uri.fromFile(f);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
            cropIntent.putExtra("pic_uri", picUri.getPath());
            Log.d("f", "=" + f.getPath());
            Log.d("picUri","="+picUri.getPath());
            pic_uri=picUri;
            startActivityForResult(cropIntent, 600);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
