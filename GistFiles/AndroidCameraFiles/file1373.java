package com.exercise.AndroidFaceDetector;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class AndroidFaceDetector extends Activity {
	
	class OurView extends SurfaceView implements SurfaceHolder.Callback{
		SurfaceHolder mHolder;
		Camera mCamera;
		public OurView(Context context) {
			super(context);
			mHolder = getHolder();
		
			mHolder.addCallback(this);
			
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// TODO Auto-generated method stub
			Camera.Parameters parameters = mCamera.getParameters();
			//parameters.setPreviewSize(w, h);
			//mCamera.setParameters(parameters);
			mCamera.startPreview();
		}
		
		
    	
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			Log.v("dan","asdasd");
			mCamera = Camera.open();
			
			
			mCamera.getParameters();
			
			try{
				mCamera.setPreviewDisplay(holder);
				Log.v("dan","I am here");
				PreviewCallback mPreviewCallback = new PreviewCallback() {		
								
								@Override
								public void onPreviewFrame(byte[] data, Camera camera) {
									Log.v("Recog","Iamher100");
									int height,width;
									//Parameters camPar=camera.getParameters();
									height=camera.getParameters().getPreviewSize().height;
									width=camera.getParameters().getPreviewSize().width;
									final int[] rgb = stripYUV(data, width, height);
									Bitmap bmp = Bitmap.createBitmap(rgb, width, height,Bitmap.Config.ARGB_8888);
									Face[] myFace = new FaceDetector.Face[5];
									   Log.v("Recog","Iamhere7");
									   FaceDetector myFaceDetect = new FaceDetector(width, height, 5);
									   Log.v("Recog","Iamhere8");
									   int numberOfFaceDetected = myFaceDetect.findFaces(bmp, myFace);
									   
									//first convert data(YUV format) to bitmap format
									/*
										Bitmap factoryOut = BitmapFactory.decodeByteArray(data,0,data.length);
										int numberOfFace,imageWidth,imageHeight;
										
									   imageWidth = factoryOut.getWidth();
									   Log.v("Recog","Iamhere5");
									   imageHeight = factoryOut.getHeight();
									   Log.v("Recog","Iamhere6");
									   
									*/
								
								}
							};
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		        Log.v("dan","asdasd2");
			
		}
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			mCamera.stopPreview();
			//Log.v(TAG, "destroyed");
			mCamera = null;
		}
		
		
		
	}
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v("dan","asdasd0");
    	super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        Log.v("dan","asdasd-1");
        OurView ourView=new OurView(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      //  SurfaceHolder mHolder=getHolder();
        //Camera mCamera=Camera.open();
        //mCamera.startPreview();
        /*
        SurfaceHolder.Callback mHolderCallback = new SurfaceHolder.Callback() {	
        	//Log.v("dan","asdasd-2");
        	//Camera mCamera;
        	//Log.v("dan","asdasd-3");
        	@Override
        	public void surfaceCreated(SurfaceHolder holder) {
        			

        	}

        	PreviewCallback mPreviewCallback = new PreviewCallback() {		
        		@Override
        		public void onPreviewFrame(byte[] data, Camera camera) {
        		
        		}
        	};

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}};
		*/
			setContentView(ourView);
        //setContentView(new myView(this));
        //Log.v("Recog","Iamhere10");
    }
    
    public static int[] stripYUV(byte[] yuv, int width, int height) throws NullPointerException, IllegalArgumentException 
    {
    	int[] rgb=new int[height*width];
		int Y;
		for(int j = 0; j < height; j++) 
		{
			int pixPtr = j * width;
			for(int i = 0; i < width; i++) 
			{
				Y = yuv[pixPtr]; if(Y < 0) Y += 255;
				rgb[pixPtr++] = 0xff000000 + Y*0x00010101;
			}
		}
		return rgb;
    }
    
    private class myView extends View{
     
     private int imageWidth, imageHeight;
     private int numberOfFace = 5;
     private FaceDetector myFaceDetect; 
     private FaceDetector.Face[] myFace;
     float myEyesDistance;
     int numberOfFaceDetected;
     
     Bitmap myBitmap;

  public myView(Context context) {
   super(context);
   // TODO Auto-generated constructor stub
   Log.v("Recog","Iamhere1");
   BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
   Log.v("Recog","Iamhere2");
   BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
   Log.v("Recog","Iamhere3");
   myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.face5b, BitmapFactoryOptionsbfo);
   Log.v("Recog","Iamhere4");
   imageWidth = myBitmap.getWidth();
   Log.v("Recog","Iamhere5");
   imageHeight = myBitmap.getHeight();
   Log.v("Recog","Iamhere6");
   myFace = new FaceDetector.Face[numberOfFace];
   Log.v("Recog","Iamhere7");
   myFaceDetect = new FaceDetector(imageWidth, imageHeight, numberOfFace);
   Log.v("Recog","Iamhere8");
   numberOfFaceDetected = myFaceDetect.findFaces(myBitmap, myFace); 
   Log.v("Recog","Iamhere9");
  }

  
  
  @Override
  protected void onDraw(Canvas canvas) {
   // TODO Auto-generated method stub
   
            canvas.drawBitmap(myBitmap, 0, 0, null);
            
            Paint myPaint = new Paint();
            Paint textPaint = new Paint();
            textPaint.setColor(Color.RED);
            textPaint.setStyle(Paint.Style.STROKE);
            myPaint.setColor(Color.RED);
            myPaint.setStyle(Paint.Style.STROKE); 
            
            
            for(int i=0; i < numberOfFaceDetected; i++)
            {
            	
            
             
             
            	
             Face face = myFace[i];
             PointF myMidPoint = new PointF();
             face.getMidPoint(myMidPoint);
             myEyesDistance = face.eyesDistance();
    		Log.v("dan","test1");
             myPaint.setStrokeWidth(myEyesDistance/5);
             if(i == 1)
            	 myPaint.setColor(Color.GREEN);
             if(i==2)
            	 myPaint.setColor(Color.BLUE);
             canvas.drawCircle(
    				(int)myMidPoint.x, (int)myMidPoint.y,
    				(int)2*myEyesDistance, myPaint);
             
             Log.v("dan","test2"); 
             //canvas.drawText("You fail", myMidPoint.x, myMidPoint.y, textPaint);
             Button btn = new Button(this.getContext());
             Log.v("dan","test3");
             btn.setText("TOUCH ME");
             Log.v("dan","test4");
             btn.setTextColor(Color.YELLOW);
             Log.v("dan","test5");
             FrameLayout flayout = (FrameLayout)findViewById(R.id.layout);
             Log.v("dan","test6");
            // flayout.addView(btn);
             Log.v("dan","test7");
             
             
             /*canvas.drawRect(
               (int)(myMidPoint.x - 1.5*myEyesDistance),
               (int)(myMidPoint.y - 2*myEyesDistance),
               (int)(myMidPoint.x + 1.5*myEyesDistance),
               (int)(myMidPoint.y + 2*myEyesDistance),
               myPaint);*/
    		 Log.v("face", "Starting face # " + i);
             Log.v("face", "eyes width: " + myEyesDistance);
             Log.v("face", "Midpoint:\n\tX: " + myMidPoint.x + "\n\tY: " + myMidPoint.y);
             Log.v("face", "Stroke width: " + myPaint.getStrokeWidth());
             Log.v("face", "*************************************");
            
             
             //new code--crosshairs
             int girth = (int)myEyesDistance/2;
             int dist = (int)myEyesDistance*(3/2);
             
             /*
             //draw top line
             canvas.drawLine(myMidPoint.x, 
            		 myMidPoint.y - dist, 
            		 myMidPoint.x, 
            		 myMidPoint.y - dist - 2*(int)myEyesDistance,
            		 myPaint);
             //draw bottom line
             canvas.drawLine(myMidPoint.x, 
            		 myMidPoint.y + dist, 
            		 myMidPoint.x, 
            		 myMidPoint.y + dist + 2*(int)myEyesDistance,
            		 myPaint);
             //draw right line
             canvas.drawLine(myMidPoint.x + dist, 
            		 myMidPoint.y, 
            		 myMidPoint.x + dist + 2*(int)myEyesDistance, 
            		 myMidPoint.y,
            		 myPaint);
           //draw left line
             canvas.drawLine(myMidPoint.x - dist, 
            		 myMidPoint.y, 
            		 myMidPoint.x - dist - 2*(int)myEyesDistance, 
            		 myMidPoint.y,
            		 myPaint);
             	*/
             
             	//add button code
                
            
            }
  		}
    }
}