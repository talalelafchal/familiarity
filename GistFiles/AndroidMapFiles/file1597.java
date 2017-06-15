package com.example.android.myproject1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Open a url link to your browser
    public void openBrowser(View v) {

        String url = (String) v.getTag();
        Intent operUrl = new Intent(Intent.ACTION_VIEW);
        operUrl.addCategory(Intent.CATEGORY_BROWSABLE);
        operUrl.setData(Uri.parse(url));
        startActivity(operUrl);
    }

    //Call a phone number (instead of a variable with the phone id, I used a static phone to make sure no calls will be made to the actual store). Also had to make this work I added to the Manifest.xml file the following: <uses-permission android:name="android.permission.CALL_PHONE"/>
    public void call(View v) {
        Intent callPhone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+00000000000"));
        startActivity(callPhone);
    }

    //Open map using the longtitude and landtitude of the store
    public void map(View v) {
        Intent openMap = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=39.666655, 20.852647"));
        openMap.setPackage("com.google.android.apps.maps");
        startActivity(openMap);
    }

    //Send an email (instead of a variable with the store's email address, I used my email to make sure no emails will be made to the actual store)
    public void email(View v) {
        Intent sentEmail = new Intent(Intent.ACTION_SEND);
        sentEmail.setType("message/rfc822");
        sentEmail.putExtra(sentEmail.EXTRA_EMAIL, new String[]{"konstantina.christoforidou@gmail.com"});
        startActivity(sentEmail);

    }

}