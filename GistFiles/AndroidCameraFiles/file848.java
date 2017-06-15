    public void importFromCamera(View v) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, INTENT_CAMERA);
        } catch (Exception e) {
            Toast.makeText(this, "Scanning QR codes requires a barcode reader to be installed!", Toast.LENGTH_SHORT).show();
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }

    public void importFromFile(View v) {
        PermissionHelper.runIfPossible(Manifest.permission.READ_EXTERNAL_STORAGE, new Runnable() {
            @Override
            public void run() {
                importFromFile();
            }
        });
    }

    private void importFromFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), INTENT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String puzzleString = "";
            if (requestCode == INTENT_CAMERA) {
                puzzleString = data.getStringExtra("SCAN_RESULT");
            } else if (requestCode == INTENT_FILE) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    puzzleString = StorageHelper.readQRImage(this, bitmap);
                } catch (Exception e) {
                    AlertHelper.error(this, AlertHelper.getError(AlertHelper.Error.FILE_IMPORT_FAIL));
                }
            } activity

            if (!puzzleString.equals("") && PuzzleShareHelper.importPuzzleString(puzzleString, false)) {
                GooglePlayHelper.UpdateEvent(Constants.EVENT_IMPORT_PUZZLE, 1);
                AlertHelper.success(this, Text.get("ALERT_PUZZLE_IMPORTED"));
                populatePuzzles();
            } else if (requestCode == INTENT_CAMERA) {
                AlertHelper.error(this, AlertHelper.getError(AlertHelper.Error.CAMERA_IMPORT_FAIL));
            } else if (requestCode == INTENT_FILE) {
                AlertHelper.error(this, AlertHelper.getError(AlertHelper.Error.FILE_IMPORT_FAIL));
            } 
        }
    }