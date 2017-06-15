package com.example.dzq.basedemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ScrollView sv;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView);
        tv.append("这是增加的内容");
        sv = (ScrollView) findViewById(R.id.scrollView);  sv.

    }
}
