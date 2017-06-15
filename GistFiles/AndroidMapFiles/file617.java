package net.abdulaziz.turorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by AbdulazizHQ on 6/20/14.
 */
public class SecondActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        Thread Timer = new Thread(){
          public void run(){
              try
              {
                  sleep(5000);
              }
              catch (InterruptedException e)
              {
                  e.printStackTrace();
              }
              finally
              {
                  Intent openActivity = new Intent("net.abdulaziz.turorial.MainActivity");
                  startActivity(openActivity);
              }
          }
        };
        Timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
