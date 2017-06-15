package me.sotm.practice.camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private ImageView _imageView;
    private Uri       _imageUri;
    private Uri       _cropedImageUri;

    private String    _saveDirectory;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this._imageView = (ImageView) this.findViewById(R.id.image_view);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        stringBuilder.append("/");
        stringBuilder.append(this.getString(R.string.app_name));
        this._saveDirectory = stringBuilder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
        case R.id.menu_camera:
            this._imageUri = ExternalAppOpener.openCamera(this);
            break;
        case R.id.menu_gallery:
            ExternalAppOpener.openGallery(this);
            break;
        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Activity.RESULT_OK == resultCode) {
            if (requestCode == ExternalAppOpener.REQUEST_GALLERY || requestCode == ExternalAppOpener.REQUEST_CAMERA) {
                this._imageUri = this._getImageUri(data);

                if (null != this._imageUri) {
                    this._cropedImageUri = this._savePictureToExternalStrage(this._imageUri);
                    ExternalAppOpener.openCrop(this, this._cropedImageUri);
                }
            } else if (requestCode == ExternalAppOpener.REQUEST_CROP) {

                if (null != this._cropedImageUri) {
                    this._imageView.setImageURI(this._cropedImageUri);
                }

            }
        }
    }

    /**
     * 画像取得
     *
     * @param data
     * @return
     */
    private Uri _getImageUri(Intent data) {
        Uri result = null;
        if (null != data) {
            result = data.getData();
        } else if (null != this._imageUri) {
            result = this._imageUri;
        } else {
            ContentResolver contentResolver = this.getContentResolver();
            Cursor cursor = MediaStore.Images.Media.query(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                    MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            result = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        }
        return result;
    }

    /**
     * クロップように外に書き出す
     *
     * @param sourceUri
     * @return
     */
    private Uri _savePictureToExternalStrage(Uri sourceUri) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = simpleDateFormat.format(new Date()) + ".jpg";
        String filePath = this._saveDirectory + "/" + fileName;
        ContentResolver contentResolver = this.getContentResolver();
        Uri uri = null;
        try {
            File dir = new File(this._saveDirectory);
            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(this._saveDirectory, fileName);

            if (file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                inputStream = contentResolver.openInputStream(sourceUri);
                byteArrayOutputStream = new ByteArrayOutputStream();
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int len = 0;
                while (inputStream.available() > 0 && (len = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                outputStream.write(byteArrayOutputStream.toByteArray());
            }
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, filePath);
            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        return uri;
    }

}
