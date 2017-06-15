package com.lucapinta.loadshow;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class ImageShow extends Activity {
 
  private static final String JPEG_PICTURE ="JPEG_PICTURE";
 private Bitmap bitmap; // Img Originale

 byte [] jpegPicture;
   
   Button button1, button2, button3;
   
   ImageView imageView;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // This is the only way camera preview work on all android devices at full screen
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       
        // No title, no name: Full screen
       getWindow().setFormat(PixelFormat.TRANSLUCENT);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
           // Set the default layout for this activity
        //this.setContentView(R.layout.imageview); // try different set -- FULL SCREN
        
        this.setContentView(R.layout.imageview2); // try different set -> image centred
        
   
    imageView = (ImageView)findViewById(R.id.imageView);
    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    
        
//********** Get JPEG image from extras from the Intent that launch this activity
         Bundle bundleExtras = getIntent().getExtras();
         if (bundleExtras.containsKey(JPEG_PICTURE)) {
                 jpegPicture = bundleExtras.getByteArray(JPEG_PICTURE);                          
                         int offset = 0;
                         int length = jpegPicture.length;
                         
                // Obtain bitmap from the JPEG data object
                bitmap = BitmapFactory.decodeByteArray(jpegPicture, offset, length);
                imageView.setImageBitmap(bitmap);
           } 
         else {
              // JPEG data is not in the bundle extra received
              finishActivity(RESULT_CANCELED);
      }//end else

         
} //end onCreate --


      //**************  Change Configurations    
            @Override 
            public void onConfigurationChanged(Configuration newConfig) {
                super.onConfigurationChanged(newConfig);
            }//end methods      
      
      
} //end class    
