package com.ti.nfcdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class rw_operations_activity extends Activity {

    Button button_read;
    Button button_write;
    Button button_blank;
    Button button_ti_com;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_read_write_layout);

        button_read = (Button) findViewById(R.id.button_nfc_read);
        button_write = (Button) findViewById(R.id.button_nfc_write);
        button_blank = (Button)findViewById(R.id.button_nfc_blank);
        button_ti_com = (Button)findViewById(R.id.button_nfc_ti_com1);

        button_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.

                Intent mynewintent = new Intent(getApplicationContext(), nfc_read_operation.class);
                mynewintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mynewintent);
            }
        });

        button_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                Intent mynewintent = new Intent(getApplicationContext(), nfc_write_operation.class);
                mynewintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(mynewintent);
            }
        });
    }

}
