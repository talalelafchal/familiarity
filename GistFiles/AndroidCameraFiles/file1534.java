final Context context = this;

ContentResolver contentResolver = context.getContentResolver();
String[] columns = new String[] {
  MediaStore.Images.ImageColumns._ID,
  MediaStore.Images.ImageColumns.TITLE,
  MediaStore.Images.ImageColumns.DATA,
  MediaStore.Images.ImageColumns.MIME_TYPE,
  MediaStore.Images.ImageColumns.SIZE,
  MediaStore.Images.ImageColumns.DATE_TAKEN,
  MediaStore.Images.ImageColumns.LATITUDE,
  MediaStore.Images.ImageColumns.LONGITUDE,
  MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
};

String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?";
String[] selectionArgs = new String[] {
  "Camera"
};

Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, selectionArgs, null);

Log.d("ListImagesActivity" + ":getCount", "" + cursor.getCount());

if(cursor.moveToFirst()){
    do{
      Log.d("ListImagesActivity" + ":image:", cursor.getString(0) + ' ' + cursor.getString(5)
        + ' ' + cursor.getString(4) + ' ' + cursor.getString(8)
        + ' ' + cursor.getString(6) + ' ' + cursor.getString(7));
    }while (cursor.moveToNext());
}