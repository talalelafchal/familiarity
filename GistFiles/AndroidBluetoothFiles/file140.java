package org.twbbs.chyen.android.BluetoothTest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothTest extends Activity {

    private TextView tMac;
    private TextView tStatus;
    private Button bSearch;
    private Button bSend;
    private ListView lResult;
    private EditText eMessage;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter myBluetoothAdapter;
    private ArrayAdapter<String> BTArrayAdapter;
    private ArrayList<Object> BTResultMac;
    private CountDownTimer scanTimer;
    private BluetoothSocket btSocket;
    private OutputStream os;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tMac = (TextView) findViewById(R.id.Mac);
        tStatus = (TextView) findViewById(R.id.Status);
        bSearch = (Button) findViewById(R.id.Search);
        bSend = (Button) findViewById(R.id.Send);
        lResult = (ListView) findViewById(R.id.Result);
        eMessage = (EditText) findViewById(R.id.Message);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bSend.setEnabled(false);
        if(myBluetoothAdapter == null) {
            bSearch.setEnabled(false);
            eMessage.setEnabled(false);
            tStatus.setText("Status: not supported");
        } else {
            if(!myBluetoothAdapter.isEnabled())
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
            tStatus.setText("Status: BlueTooth Enabled");
            BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            BTResultMac = new ArrayList<Object>();
            lResult.setAdapter(BTArrayAdapter);
        }


        lResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                BluetoothDevice device = myBluetoothAdapter.getRemoteDevice((String) BTResultMac.get(pos));
                if(myBluetoothAdapter.isDiscovering()){
                    myBluetoothAdapter.cancelDiscovery();
                    bSearch.setEnabled(true);
                }

                try {
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e) {
                    Log.d("Exception", "socket create failed: " + e.getMessage() + ".");
                }

                tStatus.setText("Status: Connecting...");
                try {
                    btSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                    Log.d("Exception", "Connection failure.");
                    tStatus.setText("Status: Connecting Failed");
                    return;
                }
                tStatus.setText("Status: Connecting finished");
                tMac.setText("Mac: " + BTResultMac.get(pos));
                bSend.setEnabled(true);
                try {
                    os = btSocket.getOutputStream();
                } catch (IOException e) {
                    Log.e("Exception", "Unable to get output stream of bluetooth socket");
                    e.printStackTrace(System.err);
                }
            }
        });
    }

    public void SearchClick(View view){
        BTArrayAdapter.clear();
        BTResultMac.clear();
        myBluetoothAdapter.startDiscovery();
        registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        tStatus.setText("Status: Discovering...");
        bSearch.setEnabled(false);

        scanTimer = new CountDownTimer(15000,1000){
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish(){
                bSearch.setEnabled(true);
                myBluetoothAdapter.cancelDiscovery();
                tStatus.setText("Status: Search Finished");
                bSearch.setEnabled(true);
            }
        };
        scanTimer.start();
    }

    public void SendClick(View arg0) {
        if (os != null) {
            try {
                String content = eMessage.getText().toString();
                if(content.length() > 0) {
                    os.write(content.getBytes());
                    eMessage.setText("");
                } else {
                    btSocket.close();
                    os = null;
                    bSend.setEnabled(false);
                }
            } catch (IOException e) {
                Log.e("Exception", "Failed to write messages");
                e.printStackTrace(System.err);
            }
        }
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
                BTResultMac.add(device.getAddress());
            }
        }
    };

}