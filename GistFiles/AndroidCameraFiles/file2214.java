package com.example.delle4310.wsepinm;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class ActivityTwo extends AppCompatActivity {
    public static int ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        String content = getIntent().getExtras().getString("content");

        TextView tv = (TextView)findViewById(R.id.tvTwo);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            tv.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tv.setText(Html.fromHtml(content));
        }
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
