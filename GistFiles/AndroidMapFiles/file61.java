class ImageActivity...
{
......
    final int PHOTO_PICKER_ID = 0;

    public void chooseImage(View view) {

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PHOTO_PICKER_ID+Integer.parseInt((String)view.getTag()));
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private HashMap<Integer, String> loadImagesMap = new HashMap<Integer, String>();

    private void importImagePerm(int imageNo, String filePath)
    {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            loadImagesMap.put(imageNo, filePath);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }else {
            importImage(imageNo, filePath);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(requestCode>=PHOTO_PICKER_ID && requestCode<PHOTO_PICKER_ID+Hero.NUM_OF_IMAGE_BUTTONS)
        {
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    importImagePerm(requestCode - PHOTO_PICKER_ID - 1, filePath);
                }
        }
    }

    private void importAllImages()
    {
        for(Integer i:loadImagesMap.keySet()) {
            importImage(i.intValue(), loadImagesMap.get(i));
        }
        loadImagesMap.clear();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    importAllImages();
                } else {
                    // Permission Denied
                    Toast.makeText(ImageActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    void importImage(int imageNo, String filePath) {
        //Bitmap img = BitmapFactory.decodeFile(filePath);
        Log.v("CalcActivity", "importImage "+imageNo+" << "+filePath);
        ImageView imgButton = (ImageView)this.findViewById(imageButtonIDs[imageNo]);
        //Drawable drw = Drawable.createFromPath(filePath);//new BitmapDrawable(getResources(), img);
        //imgButton.setImageBitmap(img);
        //imgButton.setImageResource(drw);
        //imgButton.setImageDrawable(null);
        //imgButton.setBackgroundDrawable(null);
        //Bitmap scaled = Bitmap.createScaledBitmap(img, 100, 100, true);
        //imgButton.setImageDrawable(new BitmapDrawable(scaled));
        //imgButton.setLayoutParams(new LayoutParams(
        //        LayoutParams.MATCH_PARENT,
        //        LayoutParams.WRAP_CONTENT));
        //imgButton.setImageBitmap(scaled);
        new BitmapWorkerTask(getWindow(),imgButton).execute(filePath);
    }

//...
}
================
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context=".ImageActivity"
    android:id="@+id/imageRoot">


        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">
            android:layout_height="match_parent"

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:layout_weight="1">

                <ImageView
                    android:id="@+id/imageButton1"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:background="@drawable/ic_arrow_back_24dp"
                    android:scaleType="centerCrop"
                    android:onClick="chooseImage"
                    android:tag="1"
                    android:layout_weight="1" />
            </LinearLayout>

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:layout_weight="1">

                <ImageView
                    android:id="@+id/imageButton2"
                    android:layout_width="match_parent"
                    android:background="@drawable/ic_done_24dp"
                    android:layout_height="match_parent"
                    android:scaleType="centerCrop"
                    android:onClick="chooseImage"
                    android:tag="2"
                    android:layout_weight="1" />

            </LinearLayout>


            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:layout_weight="1">

                <ImageView
                    android:id="@+id/imageButton3"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:background="@drawable/ic_done_24dp"
                    android:scaleType="centerCrop"
                    android:onClick="chooseImage"
                    android:tag="3"
                    android:layout_weight="1"/>
            </LinearLayout>
        </LinearLayout>
</RelativeLayout>
=================
class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String fileName;
    private static final String TAG = "CalcActivity";
    private final Window view;
    public BitmapWorkerTask(Window v, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        view = v;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        fileName = params[0];
        return decodeSampledBitmapFromResource(fileName, 100, 100);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setTag(null);
                imageView.setImageBitmap(null);
                imageView.setVisibility(ImageView.GONE);
                imageView.setVisibility(ImageView.VISIBLE);
                imageView.setImageDrawable(new BitmapDrawable(imageView.getResources(), bitmap).mutate());
                imageView.setBackgroundDrawable(new BitmapDrawable(imageView.getResources(), bitmap));
                imageView.setImageBitmap(bitmap);
                //imageView.setImageResource(R.drawable.ic_arrow_back_24dp);
                Log.v(TAG, "imageView setImageBitmap" + imageView.getDrawable() + ", w " + imageView.getWidth() + " h " + imageView.getHeight());
                imageView.invalidate();
                imageView.postInvalidate();
                imageView.requestLayout();
                //imageView.invalidate();
                //view.getDecorView().invalidate();
                imageView.requestLayout();
                view.findViewById(android.R.id.content).invalidate();
            }
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String fileName,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(fileName, options);
    }
}