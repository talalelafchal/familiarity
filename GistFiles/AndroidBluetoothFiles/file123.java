package com.example.ted.bluetooth_onoff;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  final static String tag = "BluetoothApp";
  final static int REQUEST_RESULT = 1;

  BluetoothAdapter bta;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    bta = BluetoothAdapter.getDefaultAdapter();
    String statustext = "";
    if(bta.isEnabled()) {
      statustext = String.format("BLuetooth %s is enabled, addr: %s", bta.getName(), bta.getAddress());
    }
    else {
      statustext = "Radio is either off or not present at all";
    }
    Log.d(tag, statustext);
    Toast.makeText(this, statustext, Toast.LENGTH_LONG).show();
  }

  public void connect(View v) {

    String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
    String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
    IntentFilter filter = new IntentFilter(actionStateChanged);
    registerReceiver(btReceiver, filter);

    startActivityForResult(new Intent(actionRequestEnable), REQUEST_RESULT);
  }

  public void disconnect(View v) {
    bta.disable();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_RESULT) {
      Log.d(tag, "request okay");
    }
    else {
      Log.d(tag, "request not okay");
    }
  }

  BroadcastReceiver btReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

      String statetext = "";
      int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1 );

      switch(state) {
        case BluetoothAdapter.STATE_TURNING_ON:
          statetext = "Bluetooth turning on";
          break;
        case BluetoothAdapter.STATE_ON:
          statetext = "Bluetooth on";
          break;
        case BluetoothAdapter.STATE_TURNING_OFF:
          statetext = "Bluetooth turning off";
          break;
        case BluetoothAdapter.STATE_OFF:
          statetext = "Bluetooth off";
          break;
      }
      Log.d(tag, statetext);
      Toast.makeText(context, statetext, Toast.LENGTH_LONG).show();
    }
  };

}
