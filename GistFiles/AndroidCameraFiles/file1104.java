package com.example.fm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by MKSoft01 on 10/23/13.
 */
public class RetrievePassword extends Activity {

    EditText Phone,EmailId;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        Phone=(EditText)findViewById(R.id.retEtPhNo);
        EmailId=(EditText)findViewById(R.id.retEtEmail);

        Phone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String phoneNo = Phone.getText().toString();
                if (phoneNo.length() > 0)
                {
                    //TODO read Databse and validate the phone number and send the pin number
                    String message = "Hello World!";

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                }
              else  if (EmailId.length() > 0)
                {
//TODO  email the pin when the entered email matches with the DB and Pin
                }
                else
                {
                    Toast.makeText(getBaseContext(),"Please enter the detail",Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(), RetrievePassword.class);
                startActivity(intent);

            }
        });



}
}