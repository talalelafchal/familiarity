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
 * Time: 8:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class nfc_write_operation extends Activity {

    Button button_rtd_operations;
    Button button_wifi_configuration;
    Button button_file_transfer;
    Button button_ti_com;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_write_operation);

        button_rtd_operations = (Button) findViewById(R.id.button_rtd_operations);
        button_wifi_configuration = (Button) findViewById(R.id.button_wifi_configurations);
        button_file_transfer = (Button)findViewById(R.id.button_file_transfer);
        button_ti_com = (Button)findViewById(R.id.button_write_oper_nfc_ti_com);


        button_rtd_operations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                Intent mynewintent = new Intent(getApplicationContext(), rtd_operations_activity.class);
                mynewintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mynewintent);
            }
        });
    }
}