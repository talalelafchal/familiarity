private void pickupImage() {
  Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  
  startActivityForResult(intent, Constants.REQUEST_CODE.LOAD_IMG);
}

private void captureImage() {
   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
   startActivityForResult(intent, Constants.REQUEST_CODE.CAMERA);
}

 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE.LOAD_IMG) {
            if (resultCode == Activity.RESULT_OK) {
                // List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String avatarPath = cursor.getString(columnIndex);

                if (!Utils.isEmpty(avatarPath)) {
                    String pathAvt = Uri.fromFile(new File(avatarPath));
                    //Path of the image here
                }
            }
        }

        if (requestCode == Constants.REQUEST_CODE.CAMERA) {
            if (resultCode == Activity.RESULT_OK) {

                String avatarPath = Utils.getRealPathFromUri(activity, data.getData());

                if (!Utils.isEmpty(avatarPath)) {
                    String pathAvt = Uri.fromFile(new File(avatarPath));
                    //Path of the image here
                }
            }
        }
    }