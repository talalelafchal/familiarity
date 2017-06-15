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
import android.view.*;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.List;

public class MainActivity extends Activity implements View.OnTouchListener{

    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;


    Canvas canvas;
    Paint paint;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        Point point = new Point();
        Display cureentDispaly = getWindowManager().getDefaultDisplay();
        cureentDispaly.getSize(point);
        int weidth = point.x;
        int height = point.y;


        Bitmap bitmap = Bitmap.createBitmap(weidth , height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);


        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);

        imageView.setOnTouchListener(this);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                //canvas.drawLine(downx, downy, upx, upy, paint);
                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;

        }
        return true;
    }

}
