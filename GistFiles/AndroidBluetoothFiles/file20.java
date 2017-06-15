package com.bluetoothsupport.panayiotisgeorgiou.runningservices;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        TextView textManufacturer = (TextView)findViewById(R.id.manufacturer);
        TextView textModel = (TextView)findViewById(R.id.model);
        TextView textSupportBT = (TextView)findViewById(R.id.supportbt);
        TextView textSupportBTLE = (TextView)findViewById(R.id.supportbtle);

        //Get brand, manufacturer and model of your device
        textManufacturer.setText(Build.BRAND + " : " + Build.MANUFACTURER);
        textModel.setText(Build.MODEL);

        //Check if your device support BlueTooth
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            textSupportBT.setText("Support BLUETOOTH");
        }else{
            textSupportBT.setText("NOT Support BLUETOOTH");
        }

        //Check if your device support Bluetooth Low Energy
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            textSupportBTLE.setText("Support BLUETOOTH_LE");
        }else{
            textSupportBTLE.setText("NOT Support BLUETOOTH_LE");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
