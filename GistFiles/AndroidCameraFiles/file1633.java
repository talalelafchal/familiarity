package com.lxy.media;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.*;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.GetChars;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.List;

public class MainActivity extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Bitmap bitmap = Bitmap.createBitmap(400 ,400, Bitmap.Config.ARGB_8888);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);


        Canvas canvas = new Canvas(bitmap);
        Paint paint  = new Paint();
        paint.setColor(Color.GREEN);
        //paint.setTypeface(Typeface.DEFAULT_BOLD);
      //  paint.setStyle(Paint.Style.STROKE);

        Typeface lovettf = Typeface.createFromAsset(getAssets(), "love.ttf");
        paint.setTypeface(lovettf);
        paint.setTextSize(40);
        paint.setStrokeWidth(10);
       // canvas.drawPoint(150, 150, paint);
        canvas.drawText("I LOVE YOU", 100, 100, paint);

        imageView.setImageBitmap(bitmap);




    }
}
