package cl.cutiko.magicalcamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.frosquivel.magicalcamera.Functionallities.PermissionGranted;
import com.frosquivel.magicalcamera.MagicalCamera;

public class MainActivity extends AppCompatActivity {

    private int RESIZE_PHOTO_PIXELS_PERCENTAGE = 150;
    private MagicalCamera magicalCamera;
    private ImageView imageView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.photoIv);

        PermissionGranted permissionGranted = new PermissionGranted(this);
        magicalCamera = new MagicalCamera(this,RESIZE_PHOTO_PIXELS_PERCENTAGE, permissionGranted);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                magicalCamera.takePhoto();
            }
        });
        button = (Button) findViewById(R.id.rotateBtn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        magicalCamera.resultPhoto(requestCode, resultCode, data);

        Bitmap bitmap = magicalCamera.getPhoto();

        //This can be null some times
        Toast.makeText(this, String.valueOf(magicalCamera.getPrivateInformation().getOrientation()), Toast.LENGTH_SHORT).show();

        imageView.setImageBitmap(bitmap);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This will rotate the image, the problem is, the saved image is not rotated
                //Im deliberately using again magicalCamera.getPhoto() cause I was initially just using the bitmap variable created above
                //I had to make sure about the orientation of the photo saved in the device, I even saved it again just in doubt
                imageView.setImageBitmap(magicalCamera.rotatePicture(magicalCamera.getPhoto(), MagicalCamera.ORIENTATION_ROTATE_90));
                magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(), "myPhotoName", "myDirectoryName", MagicalCamera.JPEG, true);
            }
        });

        magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(), "myPhotoName", "myDirectoryName", MagicalCamera.JPEG, true);
    }
}
