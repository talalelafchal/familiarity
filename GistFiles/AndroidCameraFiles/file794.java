package com.luseen.permissionexample;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PICTURE_REQUEST_CODE = 10;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 20;

    private String cameraPermission = Manifest.permission.CAMERA;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, cameraPermission)
                            == PackageManager.PERMISSION_GRANTED) {
                        openCamera();
                    } else {
                        String[] permission = {cameraPermission};
                        requestPermissions(permission, CAMERA_PERMISSION_REQUEST_CODE);
                    }
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Do the stuff that requires permission
                openCamera();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(cameraPermission)) {
                    //Permission denied
                    showNoPermissionDialog();
                } else {
                    //User denied permission forever
                    showWarning();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            ImageView imageview = (ImageView) findViewById(R.id.imageView);
            imageview.setImageBitmap(image);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PICTURE_REQUEST_CODE);
    }

    private void showNoPermissionDialog() {
        Toast.makeText(context, R.string.deny_text, Toast.LENGTH_SHORT).show();
    }

    private void showWarning() {
        Toast.makeText(context, R.string.forever_deny_text, Toast.LENGTH_SHORT).show();
    }
}
