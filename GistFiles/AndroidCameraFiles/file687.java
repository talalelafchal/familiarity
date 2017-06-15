package com.example.administrator.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Administrator on 2014/9/27.
 */
public class EX0407 extends Activity {
    private ImageView mImage;
    private Button mButtonLeft;
    private Button mButtonRight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ex0407);

        mButtonLeft = (Button) findViewById(R.id.myButton_left);
        mButtonRight = (Button) findViewById(R.id.myButton_right);
        mImage = (ImageView) findViewById(R.id.myImage);

        mButtonLeft.setOnClickListener(myButton);
        mButtonRight.setOnClickListener(myButton);

    }

    private Button.OnClickListener myButton = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == mButtonLeft.getId()) {
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_camera));
            } else if (view.getId() == mButtonRight.getId()) {
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_maps));
            }
        }
    };
}
