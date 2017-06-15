package az.ey.myphotodropper;

import android.Manifest;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class ReadStorage extends AppCompatActivity {

    private EyPermissions permissionRead = new EyPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, null);


    @Override
    protected void onResume() {
        super.onResume();

        this.permissionRead.doOnce(new Runnable() {
            @Override
            public void run() {
                Cursor mCursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //UserDictionary.Words.CONTENT_URI,   // The content URI of the words table
                        null,                        // The columns to return for each row
                        "_id>30000",                    // Selection criteria
                        null,                     // Selection criteria
                        null);

                if (mCursor != null) {
                    Log.i("CURSORR", "MediaStore.Images.Media count = " + mCursor.getCount());
                    int index = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    while (mCursor.moveToNext()) {
                        Log.i("CURSORR", "" +
                                " ID=" + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)) +
                                " TIME=" + mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED)) +
                                " URL=" + mCursor.getString(index) +
                                "");
                    }
                    mCursor.close();
                } else {
                    Log.i("CURSORR", "mCursor is null");
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        this.permissionRead.onRequestPermission(requestCode, grantResults);
    }

}
