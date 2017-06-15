package com.example.abhishek.assignmentwiredelta;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Ramya on 9-04-2016.
 */
public class SplashActivity extends ActionBarActivity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_DURATION = 2000;

    private static String TAG = "SdcsSplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ImageView image = (ImageView) findViewById(R.id.imgSplashLogo);
        TextView text = (TextView) findViewById(R.id.lblSplashText);
        // Hiding title bar in splash page
        getSupportActionBar().hide();
        showSplashScreen();
    }

    /**
     * This method shows splash screen for 2 seconds
     */
    private void showSplashScreen() {
        /* New Handler to start the Home selection activity
         * and close this Splash-Screen after some seconds.*/
        boolean b = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                SplashActivity.this.overridePendingTransition(R.anim.slide_in, R.anim.slide_right);

            }
        }, SPLASH_DISPLAY_DURATION);
    }
}
