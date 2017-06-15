// function to show a dialog to select video file
	private void showVideoChooserDialog() {

		final CharSequence[] options = { "From Camera", "From Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Upload!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("From Camera")) {
					Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.mp4");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				} else if (options[item].equals("From Gallery")) {
					// Intent intent = new
					// Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					// intent.setType("image/*");
					// startActivityForResult(Intent.createChooser(intent,
					// "Select File"),2);

					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
					intent.setType("video/*");
					startActivityForResult(intent, 2);
					//
					// Intent photoPickerIntent = new
					// Intent(Intent.ACTION_PICK);
					// photoPickerIntent.setType("image/*");
					// startActivityForResult(photoPickerIntent, 2);

				} else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();

	}
	
// on activity result to get file from intent data	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1) {
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.mp4")) {
						f = temp;
						break;
					}
				}
				String videoPath = f.getAbsolutePath();
				Log.d("SelectedVideoPathCamera", videoPath);
				try {
					uploadVideo(videoPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {
				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Video.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage, filePath,
						null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String videoPath = c.getString(columnIndex);
				c.close();
				Log.d("SelectedVideoPath", videoPath);
				try {
					uploadVideo(videoPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}