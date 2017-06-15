        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String filePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).toString()+"/Camera";

        File rootFolder = new File(filePath);

        File image = new File(rootFolder, "bp_" + timeStamp + ".png");
        Uri uriSavedImage = Uri.fromFile(image);

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAMERA_PIC_REQUEST);

        // save image to gallery
//        MediaStore.Images.Media.insertImage(getContentResolver(), b, Shot, NAME);
