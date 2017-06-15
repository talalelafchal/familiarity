class MyActivity{
	private String filepath ;
	private void doCapture(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String filepath = "你的圖片預計路徑";
		//請自己想辦法生成 sd card 路徑
		//關鍵字 Environment.getExternalStorageDirectory().getAbsolutePath()
		//我自己是放在
		// Environment.getExternalStorageDirectory().getAbsolutePath()
		// + "/Android/data/<package name>/files/myfilename.jpg"
		//不過這請你自己自由發揮吧，記得要存下來因為讀取時會用到

		intent.putExtra(MediaStore.EXTRA_OUTPUT,
		                Uri.fromFile(new File(filepath)));

		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			switch (resultCode) {
			case RESULT_CANCELED:
				//user canceled (by hitting back button)
				break;
			case RESULT_OK:
				// get photo from camera intent

				//從 filepath 讀檔 然後看要顯示要上傳還是要幹麼都行

                                //save to user albums,這裡 orientation 跟 loc 我偷懶了,可以自己找方法實作.
				saveMediaEntry(filepath, "<your_image_title>", "<your_image_description>", new Date().getTime(), 0, null);

			default:
				break;
			}
		}
	}

	private Uri saveMediaEntry(String imagePath, String title, String description, long dateTaken,
			int orientation, Location loc) {
		ContentValues v = new ContentValues();
		v.put(Images.Media.TITLE, title);
		v.put(Images.Media.DISPLAY_NAME, "");
		v.put(Images.Media.DESCRIPTION, description);
		v.put(Images.Media.DATE_ADDED, dateTaken);
		v.put(Images.Media.DATE_TAKEN, dateTaken);
		v.put(Images.Media.DATE_MODIFIED, dateTaken);
		v.put(Images.Media.MIME_TYPE, "image/jpeg");
		v.put(Images.Media.ORIENTATION, orientation);
		File f = new File(imagePath);
		File parent = f.getParentFile();
		String path = parent.toString().toLowerCase(Locale.TAIWAN);
		String name = parent.getName().toLowerCase(Locale.TAIWAN);
		v.put(Images.ImageColumns.BUCKET_ID, path.hashCode());
		v.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
		v.put(Images.Media.SIZE, f.length());
		f = null;
		if (loc != null) {
			v.put(Images.Media.LATITUDE, loc.getLatitude());
			v.put(Images.Media.LONGITUDE, loc.getLongitude());
		}
		v.put("_data", imagePath);
		ContentResolver c = getContentResolver();
		return c.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
	}	
}