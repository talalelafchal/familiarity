//Main Activity

public class TryGallaryActivity extends Activity {
 /** Called when the activity is first created. */
 @Override
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.main);

  Integer[] images = { R.drawable.img0001, R.drawable.img0030,
    R.drawable.img0100, R.drawable.img0130, R.drawable.img0200,
    R.drawable.img0230, R.drawable.img0300, R.drawable.img0330,
    R.drawable.img0354 };

  ImageAdapter adapter = new ImageAdapter(this, images);
  adapter.createReflectedImages();

  GalleryFlow galleryFlow = (GalleryFlow) findViewById(R.id.Gallery01);
  galleryFlow.setAdapter(adapter);
 }
}

//For Rotation make one class GalleryFlow

public class GalleryFlow extends Gallery {

    private Camera mCamera = new Camera();
    private int mMaxRotationAngle = 60;
    private int mMaxZoom = -120;
    private int mCoveflowCenter;

    public GalleryFlow(Context context) {
            super(context);
            this.setStaticTransformationsEnabled(true);
    }

    public GalleryFlow(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.setStaticTransformationsEnabled(true);
    }

    public GalleryFlow(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.setStaticTransformationsEnabled(true);
    }

    public int getMaxRotationAngle() {
            return mMaxRotationAngle;
    }

    public void setMaxRotationAngle(int maxRotationAngle) {
            mMaxRotationAngle = maxRotationAngle;
    }

    public int getMaxZoom() {
            return mMaxZoom;
    }

    public void setMaxZoom(int maxZoom) {
            mMaxZoom = maxZoom;
    }

    private int getCenterOfCoverflow() {
            return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
                            + getPaddingLeft();
    }

    private static int getCenterOfView(View view) {
            return view.getLeft() + view.getWidth() / 2;
    }

    protected boolean getChildStaticTransformation(View child, Transformation t) {

            final int childCenter = getCenterOfView(child);
            final int childWidth = child.getWidth();
            int rotationAngle = 0;

            t.clear();
            t.setTransformationType(Transformation.TYPE_MATRIX);

            if (childCenter == mCoveflowCenter) {
                    transformImageBitmap((ImageView) child, t, 0);
            } else {
                    rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
                    if (Math.abs(rotationAngle) > mMaxRotationAngle) {
                            rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
                                            : mMaxRotationAngle;
                    }
                    transformImageBitmap((ImageView) child, t, rotationAngle);
            }

            return true;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mCoveflowCenter = getCenterOfCoverflow();
            super.onSizeChanged(w, h, oldw, oldh);
    }

    private void transformImageBitmap(ImageView child, Transformation t,
                    int rotationAngle) {
            mCamera.save();
            final Matrix imageMatrix = t.getMatrix();
            final int imageHeight = child.getLayoutParams().height;
            final int imageWidth = child.getLayoutParams().width;
            final int rotation = Math.abs(rotationAngle);

           
            mCamera.translate(0.0f, 0.0f, 100.0f);

            // As the angle of the view gets less, zoom in
            if (rotation < mMaxRotationAngle) {
                    float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
                    mCamera.translate(0.0f, 0.0f, zoomAmount);
            }

           
            mCamera.rotateY(rotationAngle);
            mCamera.getMatrix(imageMatrix);
            imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
            imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
            mCamera.restore();
    }
}

//And at last Image Adapter for Reflection and Shadow Effect...

public class ImageAdapter extends BaseAdapter {

 int mGalleryItemBackground;
 private Context mContext;
 private Integer[] mImageIds;
 private ImageView[] mImages;

 public ImageAdapter(Context c, Integer[] ImageIds) {
  mContext = c;
  mImageIds = ImageIds;
  mImages = new ImageView[mImageIds.length];
 }

 public boolean createReflectedImages() {
  final int reflectionGap = 4;
  int index = 0;

  for (int imageId : mImageIds) {
   Bitmap originalImage = BitmapFactory.decodeResource(mContext
     .getResources(), imageId);
   int width = originalImage.getWidth();
   int height = originalImage.getHeight();

   Matrix matrix = new Matrix();
   matrix.preScale(1, -1);

   Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
     height / 2, width, height / 2, matrix, false);

   Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
     (height + height / 2), Config.ARGB_8888);

   Canvas canvas = new Canvas(bitmapWithReflection);

   canvas.drawBitmap(originalImage, 0, 0, null);

   Paint deafaultPaint = new Paint();
   canvas.drawRect(0, height, width, height + reflectionGap,
     deafaultPaint);

   canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

   Paint paint = new Paint();
   LinearGradient shader = new LinearGradient(0, originalImage
     .getHeight(), 0, bitmapWithReflection.getHeight()
     + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);

   paint.setShader(shader);

   paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

   canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
     + reflectionGap, paint);

   ImageView imageView = new ImageView(mContext);
   imageView.setImageBitmap(bitmapWithReflection);
   imageView.setLayoutParams(new GalleryFlow.LayoutParams(450,300));
//   imageView.setScaleType(ScaleType.MATRIX);
   mImages[index++] = imageView;
  }
  return true;
 }

 private Resources getResources() {
  // TODO Auto-generated method stub
  return null;
 }

 public int getCount() {
  return mImageIds.length;
 }

 public Object getItem(int position) {
  return position;
 }

 public long getItemId(int position) {
  return position;
 }

 public View getView(int position, View convertView, ViewGroup parent) {
  return mImages[position];
 }

 public float getScale(boolean focused, int offset) {
  return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
 ///return 1;
 }

}

//Your main.xml should be like this ...
/*
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:background="#ffffff"
    >

<com.test.gly.GalleryFlow 
 android:id="@+id/Gallery01" 
 android:layout_width="fill_parent" 
 android:layout_height="wrap_content" android:layout_centerInParent="true"/>
</RelativeLayout>  

*/