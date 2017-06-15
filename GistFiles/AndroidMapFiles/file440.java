package com.chaithu.phasorzandroid;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

public class Third extends Activity {
	
	public int x;
	public int y;
	public Paint paint;
	public Canvas canvas;
	public Bitmap mBitmap;
	public ImageView im1;
	public Bitmap newMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third);
        Button Draw = (Button) findViewById(R.id.button1);
        Button Undo = (Button) findViewById(R.id.button2);
        im1 = (ImageView) findViewById(R.id.imageView2);
        MyApp app1 = new MyApp();
    	paint = new Paint();
    	paint.setColor(Color.GREEN);
    	paint.setStyle(Paint.Style.STROKE);
        mBitmap =  app1.photo ;
        newMap = Bitmap.createBitmap(mBitmap);
        canvas = new Canvas(newMap);
        im1.setImageBitmap(newMap);
        im1.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int radius = 20;
				Log.e("detect","touch");
				x = (int) event.getX();
				y = (int) event.getY();
				Log.e("detectcoord","touchcoord");
				canvas.drawCircle(x, y, radius, paint);
				Log.e("draw","drawtouch");
				return true;
				
			}
		});
        
		

     }
    
    

}
