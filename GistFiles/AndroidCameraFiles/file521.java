package com.nyit.pd.pdbt;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;


public class NYITPDBT extends Activity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    boolean isConnected = false;

    TextView mConnStatus;
    LinearLayout mConnectedLayout;
    LinearLayout mUnconnectedLayout;
    EditText mPrePatientName;
    TextView mFinalPatientName;
    ProgressDialog pgg;
    RelativeLayout mStatusIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nyitpdbt);
        mConnStatus = (TextView) findViewById(R.id.connStatus);
        mConnectedLayout = (LinearLayout) findViewById(R.id.connectedLayout);
        mUnconnectedLayout = (LinearLayout) findViewById(R.id.unconnectedLayout);
        mPrePatientName = (EditText) findViewById(R.id.prePatientName);
        mFinalPatientName = (TextView) findViewById(R.id.finalPatientName);
        ((RadioGroup) findViewById(R.id.toggleGroup)).setOnCheckedChangeListener(ToggleListener);
        mStatusIndicator = (RelativeLayout) findViewById(R.id.statusIndicator);

        pgg = new ProgressDialog(this);
        pgg.setMessage("Establishing connection...");
        pgg.setCancelable(false);

    }

    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };

    public void onToggle(View view) {
        ((RadioGroup) view.getParent()).check(view.getId());
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            try {
                sendData(view.getTag().toString() + "*");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void onClickConnect(View view) {
        if (!isConnected && mPrePatientName.getText().toString().length() > 2) {
            pgg.show();
            findBT();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nyitpdbt, menu);
        if (!isConnected) {
            menu.findItem(R.id.action_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isConnected) {
            menu.findItem(R.id.action_disconnect).setVisible(false);
        } else {
            menu.findItem(R.id.action_disconnect).setVisible(true);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_disconnect && isConnected) {
            try {
                closeBT();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            mConnStatus.setText("No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("RN-422Bluetooth")) {
                    mmDevice = device;
                    mConnStatus.setText("Bluetooth Device Found");
                    try {
                        openBT();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                } else {
                    mConnStatus.setText("Bluetooth Device Not Found");
                }
            }
        }

    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        pgg.dismiss();
        mConnStatus.setText("Bluetooth Opened");
        mFinalPatientName.setText(mPrePatientName.getText().toString());
        mUnconnectedLayout.setVisibility(View.GONE);
        mConnectedLayout.setVisibility(View.VISIBLE);
        mStatusIndicator.setBackgroundColor(Color.GREEN);
        invalidateOptionsMenu();
        isConnected = true;

        //Add  date auto generated
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date date = new Date();
        final String mPName = mPrePatientName.getText().toString().replace(" ", "_");
        final String pName = mPName + "~" + dateFormat.format(date).toString();
        System.out.println(pName);
        sendData(pName + "*");
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            //mData.setText(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData(String dataToSend) throws IOException {
        String msg = dataToSend;//mDataToSend.getText().toString();
        System.out.println(msg);
        //msg += "\n";
        mmOutputStream.write(msg.getBytes());
        mmOutputStream.flush();
        //mConnStatus.setText("Data Sent");
    }

    void closeBT() throws IOException {
        String closeString = "99*";
        sendData(closeString);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        mConnStatus.setText("Bluetooth Closed");
        mStatusIndicator.setBackgroundColor(Color.RED);
        isConnected = false;

        mUnconnectedLayout.setVisibility(View.VISIBLE);
        mConnectedLayout.setVisibility(View.GONE);
        invalidateOptionsMenu();
    }


}
