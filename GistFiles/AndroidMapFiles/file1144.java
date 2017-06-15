package com.example.wibisono.tryhard_1;

import android.app.Notification;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;


public class MainActivity extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

        public void klikTombol (View v){

            switch (v.getId()) {
                case R.id.imageButton:
                    Intent intent = new Intent(this, stop_activity.class);
                    startActivity(intent);
                    break;

                case R.id.imageButton2:
                    intent = new Intent(this, route_activity.class);
                    startActivity(intent);
                    break;

                //  case R.id.iconART:
                //      intent = new Intent(this, Fragment3.class);
                //      startActivity(intent);
                //      break;

                default:
                    break;
            }
        }
}
