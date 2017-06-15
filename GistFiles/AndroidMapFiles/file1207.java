package com.ti.nfcdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created with IntelliJ IDEA.
 * User: Udayan
 * Date: 5/15/13
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class rtd_operations_activity extends Activity {


    Button button_txt;
    Button button_uri;
    Button button_blank;
    Button button_ti_com;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rtd_operations_layout);

        button_txt = (Button) findViewById(R.id.button_rtd_txt);
        button_uri = (Button) findViewById(R.id.button_rtd_uri);
        button_blank = (Button)findViewById(R.id.button_rtd_blank);
        button_ti_com = (Button)findViewById(R.id.button_rtd_opers_nfc_ti_com);

        button_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                Intent mynewintent = new Intent(getApplicationContext(), rtd_txt_activity.class);
                mynewintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mynewintent);
            }
        });


        button_uri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                Intent mynewintent = new Intent(getApplicationContext(), rtd_uri_activity.class);
                mynewintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mynewintent);
            }
        });



    }
}