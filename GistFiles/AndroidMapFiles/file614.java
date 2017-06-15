public static byte[] rotate(byte[] data) {
    Matrix matrix = new Matrix();
    //Device.getOrientation() is used in order to support the emulator and an actual device
    matrix.postRotate(Device.getOrientation());
    Bitmap bitmap = BitmapFactory.decodeByteArray(data, INT_START, data.length);
    if (bitmap.getWidth() < bitmap.getHeight()) {
        //no rotation needed
        return data;
    }
    Bitmap rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.getWidth(),
            bitmap.getHeight(), matrix, true
    );
    ByteArrayOutputStream blob = new ByteArrayOutputStream();
    rotatedBitmap.compress(CompressFormat.JPEG, 100, blob);
    byte[] bm = blob.toByteArray();
    return bm;
}