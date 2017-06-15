package com.example.blemqtttest;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.example.blefuelmeter.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

  private BluetoothAdapter adapter;
  private boolean scanning = false;
  private Handler handler;
  private BluetoothGatt gatt;

  private static final int STATE_DISCONNECTED = 0;
  private static final int STATE_CONNECTING = 1;
  private static final int STATE_CONNECTED = 2;

  private static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final BluetoothManager manager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

    handler = new Handler();

    adapter = manager.getAdapter();

    if (adapter == null || adapter.isEnabled()) {
      //need to enable adapter
    }
  }

  private void scanLeDevice(final boolean enable) {
    if (enable) {

      handler.postDelayed(new Runnable() {

        @Override
        public void run() {
          // TODO Auto-generated method stub
          scanning = false;
          adapter.stopLeScan(leCallback);
        }
      }, 10000);

      UUID devices[] = {UUID.fromString("ba42561b-b1d2-440a-8d04-0cefb43faece")};
      scanning = true;
      adapter.startLeScan(devices,leCallback);
    } else {
      scanning = false;
      adapter.stopLeScan(leCallback);
    }
  }

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    scanLeDevice(true);
  }

  @Override
  protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();

    if (scanning) {
      scanLeDevice(false);
    }

    if (gatt != null) {
      gatt.close();
      gatt = null;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
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

  private final LeScanCallback leCallback = new LeScanCallback() {

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
      // TODO Auto-generated method stub
      gatt = device.connectGatt(MainActivity.this, false, gattCallback);

    }
  }; 

  private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      switch(newState) {
        case STATE_DISCONNECTED:
          break;
        case STATE_CONNECTING:
          break;
        case STATE_CONNECTED:
          runOnUiThread(new Runnable() {
            public void run() {
              Toast.makeText(MainActivity.this, "Conncted", Toast.LENGTH_LONG).show();
            }
          });

          gatt.discoverServices();
          break;
      }
    };

    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      List<BluetoothGattService> services = gatt.getServices();
      if (services == null) {
        return;
      }
      for (Iterator iterator = services.iterator(); iterator.hasNext();) {
        BluetoothGattService bluetoothGattService = (BluetoothGattService) iterator
            .next();
        String uuid = bluetoothGattService.getUuid().toString();
        Log.i("SERVICE", "found service - " + uuid);
        if (uuid.equals("ba42561b-b1d2-440a-8d04-0cefb43faece")) {
          BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(UUID.fromString("6bcb06e2-7475-42a9-a62a-54a1f3ce11e6"));
          gatt.setCharacteristicNotification(characteristic, true);

          BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
          descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
          gatt.writeDescriptor(descriptor);
        }
      }
    };

    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    };

    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
      byte data[] = characteristic.getValue();
      try {
        final String val = new String(data,"UTF-8");
        Log.i("VALUE", "notify new value - " + val);
        runOnUiThread(new Runnable() {

          @Override
          public void run() {
            ((TextView)findViewById(R.id.counter)).setText(val);
          }
        });
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    };
  };
}
