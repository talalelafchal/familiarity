package net.photoapp.utilities.camera;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;

import net.photoapp.R;
import net.photoapp.activity.CropPhotoActivity;
import net.photoapp.utilities.Constants;
import net.photoapp.utilities.LocalUtilities;
import net.photoapp.utilities.Logger;

public class PhotoHandler implements Camera.PictureCallback, Constants {
  private static final String TAG = PhotoHandler.class.getName();
  private static boolean ENABLE_LOGGING = true;

  private final Activity mActivity;
  private int mAngle;
  private Bitmap mBitMap, mBitMapCrop, mBitMapRotate;

  public PhotoHandler(Activity activity, int angle) {
    this.mActivity = activity;
    this.mAngle = angle;
  }

  @Override
  public void onPictureTaken(byte[] data, Camera camera) {
    int screenWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
    int screenHeight = mActivity.getWindowManager().getDefaultDisplay().getHeight();
    Camera.Parameters parameters = camera.getParameters();
    Camera.Size s = parameters.getPictureSize();

    // create file
    String appdir = LocalUtilities.getApplicationDir(mActivity.getApplicationContext());
    File dir = new File(appdir);
    if (!dir.exists() && !dir.mkdirs()) {
      Logger.d(DEBUG_TAG, TAG + " Can't create directory to save image", ENABLE_LOGGING);
    }
    String filename = appdir + File.separator + PHOTO_TMP_FILE;
    File pictureFile = new File(filename);
    BitmapFactory.Options options = new BitmapFactory.Options();

    //Find the correct scale value
    int imageWidth = s.width;
    int scale = imageWidth / CAMERA_PHOTO_WIDTH;

    // create bitmap according to scale
    options.inSampleSize = scale;
    options.inDither = false;
    options.inPurgeable = true;
    options.inInputShareable = true;
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    mBitMap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

    int topBarHeight = (int) mActivity.getResources().getDimension(R.dimen.camera_top_bar_height);
    int bottomBarHeight = (int) mActivity.getResources().getDimension(R.dimen.camera_bottom_bar_height);

    float h = (float) (mBitMap.getHeight() * screenWidth) / mBitMap.getWidth();
    int max_h = screenHeight - topBarHeight - bottomBarHeight;
    float prop = (float) mBitMap.getWidth() / screenWidth;

    if (h > max_h) {
      mBitMapCrop = Bitmap.createBitmap(mBitMap.getWidth(), (int) (max_h * prop), Bitmap.Config.ARGB_8888);
      final Rect dest = new Rect(0, 0, mBitMap.getWidth(), (int) (max_h * prop));
      final Rect src = new Rect(0, (int) (topBarHeight * prop), mBitMap.getWidth(), (int) ((screenHeight - bottomBarHeight) * prop));
      Canvas canvas = new Canvas(mBitMapCrop);
      canvas.drawBitmap(mBitMap, src, dest, new Paint());
    } else {
      mBitMapCrop = mBitMap;
    }

    try {
      // rotate photo if rotation is not correct (we need here only portrait photo!)
      int rotation = 0;
      if (mBitMapCrop.getWidth() > mBitMapCrop.getHeight()) {
        rotation = 90;
      }
      Matrix matrix = new Matrix();
      matrix.postRotate(rotation);
      mBitMapRotate = Bitmap.createBitmap(mBitMapCrop, 0, 0, mBitMapCrop.getWidth(), mBitMapCrop.getHeight(), matrix, true);

      // recycling
      mBitMap.recycle();
      mBitMap = null;
      mBitMapCrop.recycle();
      mBitMapCrop = null;

      // save photo
      FileOutputStream fos = new FileOutputStream(pictureFile);
      mBitMapRotate.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      fos.close();

      mBitMapRotate.recycle();
      mBitMapRotate = null;

      // start new intent
      Intent intent = new Intent(mActivity.getApplicationContext(), CropPhotoActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      mActivity.getApplicationContext().startActivity(intent);
    } catch (Exception e) {
      Logger.d(DEBUG_TAG, TAG + " Photo exception: ", ENABLE_LOGGING);
    }
  }
}
