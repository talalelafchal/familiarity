package com.example.uno.bluetooth01_10;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView newDevicesTv;
    Button searchBtn;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newDevicesTv = (TextView) findViewById(R.id.tv_new_devices);
        searchBtn = (Button) findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressBarIndeterminateVisibility(true);
                setTitle("正在扫描...");
                //如果正在搜索，先取消
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothAdapter.startDiscovery();
            }
        });
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "本地蓝牙不可用", Toast.LENGTH_SHORT).show();
            finish();
        }
        String Address = bluetoothAdapter.getAddress(); //获取本机蓝牙MAC地址
        String Name = bluetoothAdapter.getName();   //获取本机蓝牙名称
        // 若蓝牙没打开
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();  //打开蓝牙，需要BLUETOOTH_ADMIN权限
        }

        // 打印信息
        Log.i("getAddress() : ", Address);
        Log.i("getName() : ", Name);

        // 注册用以接收到已搜索到的蓝牙设备的receiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        // 注册搜索完时的receiver
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //搜索到不是已经绑定的蓝牙设备
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newDevicesTv.append("Name:" + device.getName() + " MAC:" + device.getAddress() + "\n");
                    Log.i("Devices: ", device.getName());
                    Log.i("Devices Mac: ", device.getAddress());
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) { //搜索完成
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(MainActivity.this, "搜索完成", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
