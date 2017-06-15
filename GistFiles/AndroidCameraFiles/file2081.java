package dm.com.imagepixelator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {
    TextView take_picture, choose_from_gallery, show_gallery,btn_exit;
    public static Uri takenPictureUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Global.galleryDir = new File(Environment.getExternalStorageDirectory().toString() + "/ImagePixelator");
        if (!Global.galleryDir.isDirectory()) {
            Global.galleryDir.mkdirs();
            System.out.println("*******************FOLDER CREATED");
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        Global.screenW = displayMetrics.widthPixels;
        Global.screenH = displayMetrics.heightPixels;
        Global.scale = getResources().getDisplayMetrics().density;

        take_picture = (TextView) findViewById(R.id.take_picture);
        choose_from_gallery = (TextView) findViewById(R.id.choose_from_gallery);
        show_gallery = (TextView) findViewById(R.id.show_gallery);
        btn_exit = (TextView) findViewById(R.id.btn_exit);
        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    File file = new File(Global.galleryDir, "IMG_" + timeStamp + ".jpg");
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    takenPictureUri = Uri.fromFile(file);
                    startActivityForResult(takePictureIntent, 1);
            }
        });

        choose_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    takenPictureUri = null;
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 1);
            }
        });

        show_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, GalleryActivity.class));
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (takenPictureUri == null) {
            if (data != null) {
                Uri selectedMediaUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedMediaUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picturePath, options);

                //Calculate inSampleSize
                options.inSampleSize = Global.calculateInSampleSize(options, 1000, 1000);

                options.inJustDecodeBounds = false;
                BitmapFactory.decodeFile(picturePath, options);

                Global.takenOrSelectedImage = BitmapFactory.decodeFile(picturePath, options);
                startActivity(new Intent(MainActivity.this, ImageShowActivity.class));
            }
        } else {
            try {
                System.out.println("DIV takenPictureUri : " + takenPictureUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(takenPictureUri.getPath(), options);

                //Calculate inSampleSize
                options.inSampleSize = Global.calculateInSampleSize(options, 1000, 1000);

                options.inJustDecodeBounds = false;
                BitmapFactory.decodeFile(takenPictureUri.getPath(), options);

                Global.takenOrSelectedImage = BitmapFactory.decodeFile(takenPictureUri.getPath(), options);

                Matrix matrix = new Matrix();
                matrix.postRotate(getCameraPhotoOrientation(takenPictureUri));
                Global.takenOrSelectedImage = Bitmap.createBitmap(Global.takenOrSelectedImage, 0, 0, Global.takenOrSelectedImage.getWidth(), Global.takenOrSelectedImage.getHeight(), matrix, true);

                new File(takenPictureUri.getPath()).delete();
                startActivity(new Intent(MainActivity.this, ImageShowActivity.class));
            } catch (Exception e) {
                e.printStackTrace();
                takenPictureUri = null;
            }
        }
    }

    public int getCameraPhotoOrientation(Uri imageUri) {
        int rotate = 0;
        try {
            getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imageUri.getPath());

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
}
