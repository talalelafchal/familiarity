package com.example.administrator.lastapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int REQEUST_CAMERA = 2;
    ImageView imgv;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgv = (ImageView) findViewById(R.id.imgv1);
        Button btn = (Button) findViewById(R.id.btn_pickImg);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                String imgName = "IMG_" + timeStamp + ".jpg";
                File file = new File(Environment.getExternalStorageDirectory(), "DCIM/Camere/" + imgName);
                uri = Uri.fromFile(file);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(Intent.createChooser(intent1, "Take a picture with"), REQEUST_CAMERA);
            }

        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQEUST_CAMERA && resultCode == RESULT_OK){
            getContentResolver().notifyChange(uri, null);
            ContentResolver cr = getContentResolver();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                imgv.setImageBitmap(bitmap);
                Toast.makeText(getApplicationContext(), uri.getPath(), Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();

            }
        }
    }
}
