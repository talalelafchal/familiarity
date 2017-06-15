....

String realFileUri = new File(AppHelper.getDirectoryPath(), AppHelper.getFileName() + ".jpeg");

mMediaUri = FileProvider.getUriForFile(
    getContext(),
    "com.nnnnnnn.provider",
    realFileUri
);

Log.e("=====>", "Share URI: " + mMediaUri.toString());
Log.e("=====>", "Real File URI: " + realFileUri.toStrig());


Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
iCamera.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
startActivityForResult(iCamera, Constants.INTENT_CALL.CAPTURE_IMAGE);


switch (requestCode) {
    case Constants.INTENT_CALL.CAPTURE_IMAGE:
        String filePath = SiliCompressor.with(getActivity()).compress(realFileUri.toString(), true);
}
....
