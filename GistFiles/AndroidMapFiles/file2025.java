package com.sgt_tibs.demo.listcolor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by lt_tibs on 9/15/15.
 */
public class ActivityThree extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);


        Button closeActivity = (Button) findViewById(R.id.buttonCloseActivityThree);

        closeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
