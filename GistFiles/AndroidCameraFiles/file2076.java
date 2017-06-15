package com.example.administrator.test;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2014/10/4.
 */
public class EX0507 extends Activity {
    private Button mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ex0507);

        mButton = (Button) findViewById(R.id.myButton);

        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 創建ImageView */
                ImageView mView = new ImageView(EX0507.this);
                TextView mText = new TextView(EX0507.this);

                LinearLayout lay = new LinearLayout(EX0507.this);       //創建LinearLayout對象
                mText.setText("www.happy.com");

                /* 判斷mText的內容, 來與系統作連接
                * 但是, Toast裡的連結是無法點擊的*/
                Linkify.addLinks(
                        mText,Linkify.WEB_URLS |
                        Linkify.EMAIL_ADDRESSES |
                        Linkify.PHONE_NUMBERS
                );

                Toast toast = Toast.makeText(
                        EX0507.this,
                        mText.getText(),
                        Toast.LENGTH_LONG
                );

                View textView = toast.getView();                //自定義View對象
                lay.setOrientation(LinearLayout.HORIZONTAL);    //設置水平排列
                mView.setImageResource(R.drawable.ssd_camera);  //指定要顯示的圖片
                lay.addView(mView);
                lay.addView(textView);
                toast.setView(lay);
                toast.show();
            }
        });
    }
}