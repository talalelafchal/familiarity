PictureCallback myPictureCallback_JPG = new PictureCallback(){

    @Override
    public void onPictureTaken(byte[] data, Camera arg1) {
        // TODO Auto-generated method stub

        try {
            private final INT_QUALITY = 2;
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = INT_QUALITY;
            
            bitmapPicture = BitmapFactory.decodeByteArray(data, data.length,options);

            
            int dim = Math.min(bitmapPicture.getHeight(),bitmapPicture.getWidth())
            bitmapPicture = Bitmap.createBitmap(
                        bitmapPicture, 
                        0,
                     	0,
                     	dim, 
                     	dim
                     	);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
              	//create a file to write bitmap data
		File f = new File(context.getCacheDir(), filename);
		f.createNewFile();

		//Convert bitmap to byte array
		Bitmap bitmap = your bitmap;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();
		//write the bytes in file
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(bitmapdata);
		fos.flush();

       	} 	catch (IOException e) {
		    // do stuff
	} 	finally {
		    if (fos != null) {
		       	fos.close();
		    }
	}

       
    }
}