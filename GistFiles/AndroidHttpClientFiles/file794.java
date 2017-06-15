package com.baidu.demo.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.baidu.demo.R;


public class Demo extends Activity implements View.OnClickListener {

    Button b1, cube, ball,ss;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(this);

        cube = (Button) findViewById(R.id.cube_button);
        cube.setOnClickListener(this);

        ball = (Button) findViewById(R.id.ball_button);
        ball.setOnClickListener(this);

        ss = (Button) findViewById(R.id.ss_button);
        ss.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view == b1) {
            startActivity(new Intent(this, BitmapReceived.class));
        } else if (view == cube) {
            startActivity(new Intent(this, CubeDemo.class));
        } else if (view == ball) {
            startActivity(new Intent(this, OpenGLDemo.class));
        } else if (view == ss) {
            startActivity(new Intent(this, StreetScape.class));
        }
    }
}
