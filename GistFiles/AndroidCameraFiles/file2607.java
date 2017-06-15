public class TakePhotoActivity extends Activity {

    public final static String TAG = TakePhotoActivity.class.getName();

    public final static String CAMERA_PIC_URI_FOR_UPLOAD = "your.package.android.TakePhotoActivity.CAMERA_PIC_URI_FOR_UPLOAD";
    public final static String CAMERA_PIC_ORIENTATION_FOR_UPLOAD = "your.package.android.TakePhotoActivity.CAMERA_PIC_ORIENTATION_FOR_UPLOAD";

    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100; 
    public final static int MEDIA_TYPE_IMAGE = 1;
    public final static int MEDIA_TYPE_VIDEO = 2;

    private static final String DATE_CAMERA_INTENT_STARTED_STATE = "your.package.android.photo.TakePhotoActivity.dateCameraIntentStarted";
    private static Date dateCameraIntentStarted = null;
    private static final String CAMERA_PIC_URI_STATE = "your.package.android.photo.TakePhotoActivity.CAMERA_PIC_URI_STATE";
    private static Uri cameraPicUri = null;
    private static final String ROTATE_X_DEGREES_STATE = "your.package.android.photo.TakePhotoActivity.ROTATE_X_DEGREES_STATE";
    private static int rotateXDegrees = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        ViewGroup vg = (ViewGroup) findViewById(R.id.content);
        ViewGroup.inflate(TakePhotoActivity.this, R.layout.activity_take_photo_action, vg);
        super.setFooterButtonColor(R.id.footer_capture_actions_button);
        ((ImageButton) findViewById(R.id.footer_capture_actions_button)).setImageResource(R.drawable.ic_camera_grey);

        if (savedInstanceState == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                startCameraIntent();
            } else {
                showWarningDialog(getString(R.string.error_sd_card_not_mounted));
            }
        }
    }

    private void startCameraIntent() {
        try {
            dateCameraIntentStarted = new Date();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //NOTE: Do NOT SET: intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPicUri) on Samsung Galaxy S2/S3/.. for the following reasons:
            // 1.) it will break the correct picture orientation
            // 2.) the photo will be stored in two locations (the given path and additionally in the MediaStore)
            String manufacturer = android.os.Build.MANUFACTURER.toLowerCase();
            if(!(manufacturer.contains("samsung")) && !(manufacturer.contains("sony"))) {
                String filename = System.currentTimeMillis() + ".jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);
                cameraPicUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPicUri);
            }
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
         } catch (ActivityNotFoundException e) {
             showWarningDialog(getString(R.string.error_could_not_take_photo));
         }      
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (dateCameraIntentStarted != null) {
            savedInstanceState.putString(DATE_CAMERA_INTENT_STARTED_STATE, DateHelper.dateToString(dateCameraIntentStarted));
        }
        if (cameraPicUri != null) {
            savedInstanceState.putString(CAMERA_PIC_URI_STATE, cameraPicUri.toString());
        }
        savedInstanceState.putInt(ROTATE_X_DEGREES_STATE, rotateXDegrees);
    }

    @Override
    public void  onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(DATE_CAMERA_INTENT_STARTED_STATE)) {
            dateCameraIntentStarted = DateHelper.stringToDate(savedInstanceState.getString(DATE_CAMERA_INTENT_STARTED_STATE));
        }
        if (savedInstanceState.containsKey(CAMERA_PIC_URI_STATE)) {
            cameraPicUri = Uri.parse(savedInstanceState.getString(CAMERA_PIC_URI_STATE));
        }
        rotateXDegrees = savedInstanceState.getInt(ROTATE_X_DEGREES_STATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {

            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                onTakePhotoActivityResult(requestCode, resultCode, intent);
                break;
            }
        }
    }

    private void onTakePhotoActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            Cursor myCursor = null;
            Date dateOfPicture = null;
            try {
                // Create a Cursor to obtain the file Path for the large image
                String[] largeFileProjection = {MediaStore.Images.ImageColumns._ID,
                                                MediaStore.Images.ImageColumns.DATA,
                                                MediaStore.Images.ImageColumns.ORIENTATION,
                                                MediaStore.Images.ImageColumns.DATE_TAKEN};
                String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
                myCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                    largeFileProjection, null, null, largeFileSort);
                myCursor.moveToFirst();
                // This will actually give you the file path location of the image.
                String largeImagePath = myCursor.getString(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                Uri tempCameraPicUri = Uri.fromFile(new File(largeImagePath));
                if (tempCameraPicUri != null) {
                    dateOfPicture = new Date(myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)));
                    if (dateOfPicture != null && dateOfPicture.after(dateCameraIntentStarted)) {
                        cameraPicUri = tempCameraPicUri;
                        rotateXDegrees = myCursor.getInt(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION));
                    }
                }
            } catch (Exception e) {
//              Log.w("TAG", "Exception - optaining the picture's uri failed: " + e.toString());
            } finally {
                if (myCursor != null) {
                    myCursor.close();
                }
            }

            if (cameraPicUri == null) {
                try {
                    cameraPicUri = intent.getData();
                } catch (Exception e){                  
                    showWarningDialog(getString(R.string.error_could_not_take_photo));
                }
            }

            if (cameraPicUri != null) {
                Intent actionIntent = new Intent(TakePhotoActivity.this, UploadPhotoActivity.class);
                actionIntent.putExtra(CAMERA_PIC_URI_FOR_UPLOAD, cameraPicUri.toString());
                actionIntent.putExtra(CAMERA_PIC_ORIENTATION_FOR_UPLOAD, rotateXDegrees);
                startActivity(actionIntent);
                this.finish();
            } else {
                showWarningDialog(getString(R.string.error_could_not_take_photo));
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            cancelActivity();
        } else {
            cancelActivity();
        }
    }

    private void showWarningDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.warning_dialog_heading));
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                    cancelActivity();
                }
              });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();     
    }

    private void cancelActivity() {
        this.finish();
    }
}