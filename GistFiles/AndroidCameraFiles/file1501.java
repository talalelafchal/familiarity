package com.prognosticator.funme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CreatingFunActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener {

    private final String TAG = "CreatingFunActivity";
    private Context context;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mMode = NONE;

    private Camera camera = null;
    private SurfaceHolder mCameraSurfaceHolder = null;
    private boolean mPreviewing = false;

    private ImageView mFirstFunImage;
    private ImageView mSecondFunImage;
    private ImageView mThirdFunImage;
    private ImageView mFourthFunImage;
    private ImageView mFifthFunImage;
    private ImageView mSelectedFunImage;

    private boolean isLighOn = false;

    private ImageView mFlashIcon;
    private ImageView mZoomInIcon;
    private ImageView mZoomOutIcon;

    private FrameLayout.LayoutParams mFrameLayoutParams;
    private FrameLayout mFrameLayout;

    private int _xDelta;
    private int _yDelta;

    private Matrix mMatrix = new Matrix();
    private Matrix mSavedMatrix = new Matrix();
    private BitmapDrawable mBackground = null;
    private Bitmap mBackgroundBitmap = null;
    private Bitmap mBitmap = null;

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;

    String[] path;
    private String[] parseComObjectId;
    private String imageNumber;

    private void showImageView(String path, ImageView imageView) {
        if (path.equals("null")) {
            imageView.setVisibility(View.INVISIBLE);
        } else {
            Picasso.with(this).load(path).into(imageView);
            BitmapDrawable ob = new BitmapDrawable(getResources(), mBitmap);
            imageView.setBackgroundDrawable(ob);
        }
    }

    private void setBitmap(ImageView imageView) {
        mBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        mSelectedFunImage.setImageBitmap(mBitmap);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_creating_fun);

        SurfaceView cameraSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        mCameraSurfaceHolder = cameraSurfaceView.getHolder();
        mCameraSurfaceHolder.addCallback(this);
        // mCameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mFrameLayout = (FrameLayout) findViewById(R.id.containerImg);
        mFrameLayout.setDrawingCacheEnabled(true);

        mFirstFunImage = (ImageView) findViewById(R.id.first_img);
        mSecondFunImage = (ImageView) findViewById(R.id.second_img);
        mThirdFunImage = (ImageView) findViewById(R.id.third_img);
        mFourthFunImage = (ImageView) findViewById(R.id.fourth_img);
        mFifthFunImage = (ImageView) findViewById(R.id.fifth_img);
        mSelectedFunImage = (ImageView) findViewById(R.id.selectedImageView);


        camera = Camera.open();
        final Camera.Parameters p = camera.getParameters();
        mFlashIcon = (ImageView) findViewById(R.id.ic_flash);

        mFlashIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (isLighOn) {
                    Log.i("info", "flash is turn off!");
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                    //camera.stopPreview();
                    camera.startPreview();
                    isLighOn = false;
                    mFlashIcon.setImageResource(R.drawable.ic_flash_off_black);
                } else {
                    Log.i("info", "flash is turn on!");
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    camera.setParameters(p);
                    camera.startPreview();
                    isLighOn = true;
                    mFlashIcon.setImageResource(R.drawable.ic_flash_on_white);
                }

            }
        });

        // We receive and parse the selected set
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // TODO add a blank check for configuration
        Log.d(TAG, "preferences.getString(\"path\", null) " + preferences.getString("path", null));
        Log.d(TAG, "before .getInt(\"image\", 0) " + preferences.getInt("image", 0));

        Integer imageId = preferences.getInt("image", 0);

        String paths = preferences.getString("path", null);
        path = paths != null ? paths.split(" ") : new String[0];

        // Download the fun selected in the list of funs
        Picasso.with(this).load(path[imageId]).into(mSelectedFunImage);
        parseComObjectId = path[imageId].split("\\?");
        imageNumber = "";
        Log.d(TAG, "parseComObjectId[1] " + parseComObjectId[1]);
        preferences.edit().remove("image").commit(); // TODO do experiments
        Log.d(TAG, "after .getInt(\"image\", 0) " + preferences.getInt("image", 0));

        showImageView(path[0], mFirstFunImage);
        showImageView(path[1], mSecondFunImage);
        showImageView(path[2], mThirdFunImage);
        showImageView(path[3], mFourthFunImage);
        showImageView(path[4], mFifthFunImage);

        mFirstFunImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBitmap(mFirstFunImage);
                parseComObjectId = path[0].split("\\?");
                Log.d(TAG, "parseComObjectId[1] " + parseComObjectId[1]);
                imageNumber = "";
            }
        });
        mSecondFunImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBitmap(mSecondFunImage);
                parseComObjectId = path[1].split("\\?");
                Log.d(TAG, "parseComObjectId[1] " + parseComObjectId[1]);
                imageNumber = "2";
            }
        });
        mThirdFunImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBitmap(mThirdFunImage);
                parseComObjectId = path[2].split("\\?");
                Log.d(TAG, "parseComObjectId[1] " + parseComObjectId[1]);
                imageNumber = "3";
            }
        });
        mFourthFunImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBitmap(mFourthFunImage);
                parseComObjectId = path[3].split("\\?");
                Log.d(TAG, "parseComObjectId[1] " + parseComObjectId[1]);
                imageNumber = "4";
            }
        });
        mFifthFunImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBitmap(mFifthFunImage);
                parseComObjectId = path[4].split("\\?");
                Log.d(TAG, "parseComObjectId[1] " + parseComObjectId[1]);
                imageNumber = "5";
            }
        });

        //mFrameLayoutParams = new FrameLayout.LayoutParams(2048, 2048);
        mFrameLayoutParams = new FrameLayout.LayoutParams(1148, 1536);
        //mFrameLayoutParams.setMargins(300, 0, 300, 0);
        mSelectedFunImage.setLayoutParams(mFrameLayoutParams);
        mSelectedFunImage.setOnTouchListener(this);

        Button btnCapture = (Button) findViewById(R.id.button1);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, cameraPictureCallbackJpeg);

                // Retrieve the object by id
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Funs");

                query.getInBackground(parseComObjectId[1], new GetCallback<ParseObject>() {
                    public void done(ParseObject funs, ParseException e) {
                        if (e == null) {
                            int count = funs.getInt("image_used" + imageNumber);
                            funs.put("image_used" + imageNumber, count + 1);
                            funs.saveEventually();
                            Log.d(TAG, "count updated");
                            Log.d(TAG, "parseComObjectId[1] " + parseComObjectId[1]);
                        } else {
                            // something went wrong
                            Log.d("DEBUG", "ERROR WITH EXCEPTION : " + e);
                        }
                    }
                });
            }
        });
    }

    // Moving pictures on the camera
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        ImageView image = (ImageView) view;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) view
                        .getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;

                mSavedMatrix.set(mMatrix);
                start.set(event.getX(), event.getY());
                mMode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    mSavedMatrix.set(mMatrix);
                    midPoint(mid, event);
                    mMode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);

                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                mFrameLayoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                //mFrameLayoutParams.leftMargin = X - _xDelta;
                //mFrameLayoutParams.topMargin = Y - _yDelta;
                //mFrameLayoutParams.rightMargin = -250;
                //mFrameLayoutParams.bottomMargin = -250;

                if (mMode == DRAG) {
                    mMatrix.set(mSavedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    mMatrix.postTranslate(dx, dy);
                } else if (mMode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        mMatrix.set(mSavedMatrix);
                        float scale = (newDist / oldDist);
                        mMatrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null && event.getPointerCount() == 2) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        mMatrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (mSelectedFunImage.getWidth() / 2) * sx;
                        float yc = (mSelectedFunImage.getHeight() / 2) * sx;
                        mMatrix.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }
        image.setImageMatrix(mMatrix);
        return true;
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = (float) 0;
        float y = (float) 0;

        if (event.getPointerCount() >= 2) {
            x = event.getX(0) - event.getX(1);
            y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }

        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public void chooseFan(View view) {
        Intent intent = new Intent(this, ListOfFansActivity.class);
        startActivity(intent);
    }

    private void viewResults(Camera.PictureCallback view,
                             Class<ResultActivity> resultActivityClass,
                             String funPath) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("funPath", funPath);
        startActivity(intent);
    }

    Camera.ShutterCallback cameraShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    Camera.PictureCallback cameraPictureCallbackRaw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            BitmapFactory.Options opt;

            opt = new BitmapFactory.Options();
            opt.inTempStorage = new byte[16 * 1024];
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPictureSize();

            int height11 = size.height;
            int width11 = size.width;
            float mb = (width11 * height11) / 1024000;

            if (mb > 4f)
                opt.inSampleSize = 4;
            else if (mb > 3f)
                opt.inSampleSize = 2;

            //preview from camera
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
        }
    };

    private Camera.PictureCallback cameraPictureCallbackJpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap cameraBitmapNull = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            // Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            // int wid = cameraBitmap.getWidth(); // 3264
            // int hgt = cameraBitmap.getHeight(); // 2448
            Log.d(TAG, "mFrameLayout width " + mFrameLayout.getWidth());
            Log.d(TAG, "mFrameLayout height" + mFrameLayout.getHeight());

            int wid;
            int hgt = options.outHeight;
            Matrix nm = new Matrix();

            Camera.Size cameraSize = camera.getParameters().getPictureSize();
//            Camera.Parameters p = camera.getParameters();
//            p.setPreviewSize(640, 320);
//            camera.setParameters(p);

            Camera.Parameters params = camera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPictureSizes();
            Log.d(TAG, "sizes " + sizes.toString());

            float ratio;
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                nm.postRotate(90);
                nm.postTranslate(hgt, 0);
                wid = options.outHeight;
                hgt = options.outWidth;
                    ratio = mFrameLayout.getWidth() * 1f / cameraSize.height;
            } else {
                wid = options.outWidth;
                hgt = options.outHeight;
                ratio = mFrameLayout.getHeight() * 1f / cameraSize.height;
            }

            float[] f = new float[9];
            mMatrix.getValues(f);
            f[0] = f[0] / ratio;
            f[4] = f[4] / ratio;
            f[5] = f[5] / ratio;
            f[2] = f[2] / ratio;
            mMatrix.setValues(f);

            Bitmap newBitmap = Bitmap.createBitmap(wid, hgt, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            canvas.drawBitmap(cameraBitmap, nm, null);

            if (cameraBitmap != null && !cameraBitmap.isRecycled()) {
                //cameraBitmap.recycle();
                cameraBitmap = null;
            }

            if (mBitmap != null) {
                canvas.drawBitmap(mBitmap, mMatrix, null);
            } else {
//                mSelectedFunImage.buildDrawingCache();
//                mBitmap = mSelectedFunImage.getDrawingCache();

                Drawable d = mSelectedFunImage.getDrawable();
                mBitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvasFun = new Canvas(mBitmap);
                d.draw(canvasFun);
                canvas.drawBitmap(mBitmap, mMatrix, null);
            }

            mMatrix = new Matrix();

            String albumname = "funme";
            File storagePath = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), albumname);
            storagePath.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File myImage = new File(storagePath, timeStamp + ".jpg");

            galleryAddPic(myImage.getPath());

            try {
                FileOutputStream out = new FileOutputStream(myImage);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

                out.flush();
                out.close();
            } catch (IOException e) {
                Log.d("In Saving File", e + "");
            }

            camera.startPreview();
            viewResults(this, ResultActivity.class,
                    Uri.parse("file://" + myImage.getAbsolutePath()).toString());
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mPreviewing) {
            camera.stopPreview();
            mPreviewing = false;
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size bestSize;

            List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
            bestSize = sizeList.get(0);

            for (int i = 1; i < sizeList.size(); i++) {
                if ((sizeList.get(i).width * sizeList.get(i).height) >
                        (bestSize.width * bestSize.height)) {
                    bestSize = sizeList.get(i);
                }
            }

            Log.d(TAG, "bestSize " + bestSize.width + " " + bestSize.height);
            // parameters.setPreviewSize(640, 480);
            // parameters.setPictureSize(320, 240);
            //parameters.setPreviewSize(bestSize.width, bestSize.height);
//            parameters.setPreviewSize(640, 480);

            camera.setParameters(parameters);
            camera.startPreview();

            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                camera.setDisplayOrientation(90);
            }
            camera.setParameters(parameters);
            camera.setPreviewDisplay(mCameraSurfaceHolder);
            camera.startPreview();
            mPreviewing = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
//            Toast.makeText(getApplicationContext(),
//                    R.string.camera_is_not_working,
//                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        mPreviewing = false;
    }

    public void galleryAddPic(String file) {
        File f = new File(file);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        sendBroadcast(mediaScanIntent);
    }

}