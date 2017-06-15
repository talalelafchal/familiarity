        private Button buttonPilih;
	private Uri mImageCaptureUri;
	private String mPath;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private Bitmap bitmap;
      
        private void initDialog() {
		final String[] items = new String[] { "From Camera", "From SD Card" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					openCamera();
				} else {
					openGallery();
				}
			}
		});

		final AlertDialog dialog = builder.create();
		buttonPilih = (Button) findViewById(R.id.buttonPilih);
		buttonPilih.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});
	}

	private void openCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dirPath = new File(Environment.getExternalStorageDirectory()
				+ "/DIRECTORY_NAME/");
		dirPath.mkdirs(); //create the directory
		File file = new File(dirPath, "tmp_avatar_"
				+ String.valueOf(System.currentTimeMillis()) + ".jpg");
		// File file = new File(Environment.getExternalStorageDirectory(),
		// "tmp_avatar_" + String.valueOf(System.currentTimeMillis())
		// + ".jpg");
		mImageCaptureUri = Uri.fromFile(file);

		try {
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			intent.putExtra("return-data", true);

			startActivityForResult(intent, PICK_FROM_CAMERA);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void openGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, PICK_FROM_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		Bitmap bitmap = null;

		if (requestCode == PICK_FROM_FILE) {
			mImageCaptureUri = data.getData();
			mPath = getRealPathFromURI(mImageCaptureUri); // from Gallery

			if (mPath == null)
				mPath = mImageCaptureUri.getPath(); // from File Manager

			if (mPath != null)
				bitmap = BitmapFactory.decodeFile(mPath);
		} else {
			mPath = mImageCaptureUri.getPath();
			bitmap = BitmapFactory.decodeFile(mPath);
		}

		Intent intent = new Intent(MainActivity.this, EditActivity.class);
		intent.putExtra("data", mPath);
		startActivity(intent);
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);

		if (cursor == null)
			return null;

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);
	}