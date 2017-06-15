public class UploadPhotoActivity extends Activity {

    private static final String PHOTO_STATE = "your.package.android.photo.UploadPhotoActivity.photo";
    private Bitmap photo = null;
    private static final String THUMBNAIL_STATE = "your.package.android.photo.UploadPhotoActivity.thumbnail";
    private Bitmap thumbnail = null;
    private static final String CAMERA_PIC_URI_STATE = "your.package.android.photo.UploadPhotoActivity.cameraPicUri";
    private Uri cameraPicUri = null;
    private static final String ROTATE_X_DEGREES_STATE = "your.package.android.photo.TakePhotoActivity.ROTATE_X_DEGREES_STATE";
    private int rotateXDegrees = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        Intent intent = getIntent();        
        cameraPicUri = Uri.parse(intent.getStringExtra(TakePhotoActivity.CAMERA_PIC_URI_FOR_UPLOAD));
        rotateXDegrees = intent.getIntExtra(TakePhotoActivity.CAMERA_PIC_ORIENTATION_FOR_UPLOAD, 0);

        try {
            photo = BitmapHelper.readBitmap(this, cameraPicUri);
            if (photo != null) {
                photo = BitmapHelper.shrinkBitmap(photo, 600, rotateXDegrees);
                thumbnail = BitmapHelper.shrinkBitmap(photo, 100);
                ImageView imageView = (ImageView) findViewById(R.id.sustainable_action_photo);
                imageView.setImageBitmap(photo);                
            } else {
                showWarningDialog(getResources().getString(R.string.error_could_not_take_photo));
            }
        } catch (Exception e) {
            showWarningDialog(getResources().getString(R.string.error_could_not_take_photo));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (photo != null) {
            savedInstanceState.putByteArray(PHOTO_STATE, BitmapHelper.bitmapToByteArray(photo));
            BitmapHelper.clearBitmap(photo);
        }
        if (thumbnail != null) {
            savedInstanceState.putByteArray(THUMBNAIL_STATE, BitmapHelper.bitmapToByteArray(thumbnail));
            BitmapHelper.clearBitmap(thumbnail);
        }
        if (cameraPicUri != null) {
            savedInstanceState.putString(CAMERA_PIC_URI_STATE, cameraPicUri.toString());
        }
        savedInstanceState.putInt(ROTATE_X_DEGREES_STATE, rotateXDegrees);
    }

    @Override
    public void  onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(PHOTO_STATE)) {
            photo = BitmapHelper.byteArrayToBitmap(savedInstanceState.getByteArray(PHOTO_STATE));
        }
        if (savedInstanceState.containsKey(THUMBNAIL_STATE)) {
            thumbnail = BitmapHelper.byteArrayToBitmap(savedInstanceState.getByteArray(THUMBNAIL_STATE));
        }
        if (savedInstanceState.containsKey(CAMERA_PIC_URI_STATE)) {
            cameraPicUri = Uri.parse(savedInstanceState.getString(CAMERA_PIC_URI_STATE));
        }
        rotateXDegrees = savedInstanceState.getInt(ROTATE_X_DEGREES_STATE);
    }

    @Override
    protected void onDestroy() {
        ImageView imageView = (ImageView) findViewById(R.id.sustainable_action_photo);
        imageView.setImageBitmap(null); 
        super.onDestroy();
    }

}