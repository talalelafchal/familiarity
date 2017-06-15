package com.example.prakash.myapplication;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MyActivity extends Activity {

    private ImageView mImageView;
    private AnimationDrawable frameAnimation;
    private RelativeLayout mGreenLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mImageView = (ImageView)findViewById(R.id.image);
        mGreenLay = (RelativeLayout)findViewById(R.id.green_lay);


        // Setting animation_list.xml as the background of the image view
        mImageView.setBackgroundResource(R.drawable.fram_anim1);

        // Type casting the Animation drawable
       frameAnimation = (AnimationDrawable) mImageView.getBackground();

        frameAnimation.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDone(View v){



        frameAnimation.stop();

        mGreenLay.setVisibility(View.VISIBLE);
        Animation fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade);
        fadeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // Setting animation_list.xml as the background of the image view
                mImageView.setBackgroundResource(R.drawable.fram_anim2);

                // Type casting the Animation drawable
                AnimationDrawable frameAnimation = (AnimationDrawable) mImageView.getBackground();

                frameAnimation.start();
                mGreenLay.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mGreenLay.startAnimation(fadeAnimation);



    }
}
