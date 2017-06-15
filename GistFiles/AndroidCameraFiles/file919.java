camera = Camera.open(); // <8>
	    Camera.Parameters parameters = camera.getParameters();
	    camera.setDisplayOrientation(90);
	    parameters.setZoom(16);
	    parameters.setPictureFormat(PixelFormat.JPEG);
	    camera.setParameters(parameters);

--


PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) { 
			FileOutputStream outStream = null;
			boolean mExternalStorageAvailable = false;
			boolean mExternalStorageWriteable = false;
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
			    // We can read and write the media
			    mExternalStorageAvailable = mExternalStorageWriteable = true;
			    Log.d(TAG, "Can Write ");
			    try {
			    	String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
			    	String fileName = "/" + System.currentTimeMillis() + ".jpg";
			    	
			    	Log.d(TAG, "File: " + baseDir + fileName);
			    	outStream = new FileOutputStream(baseDir + fileName); 
					outStream.write(data);
					outStream.close();
				
					
					Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
			
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			    // We can only read the media
			    mExternalStorageAvailable = true;
			    mExternalStorageWriteable = false;
			    Log.d(TAG, "Cant Write ");
			} else {
			    // Something else is wrong. It may be one of many other states, but all we need
			    //  to know is we can neither read nor write
			    mExternalStorageAvailable = mExternalStorageWriteable = false;
			    Log.d(TAG, "Other Error ");
			}
				}
	};
