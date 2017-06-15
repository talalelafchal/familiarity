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

			default:
				break;
			}
		}
	}
}