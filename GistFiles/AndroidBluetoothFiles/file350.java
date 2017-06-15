package mdpandroid.mdpandroid;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.graphics.Matrix;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    //private String xposText;
    private Button possend;
    Button btn;
    BluetoothAdapter adapter;
    // private int x=0;
    EditText xpostext;
    EditText ypostext;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    // Name of the connected device
    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    BluetoothConnect BC;
    PixelGridView pgv;
    FunctionPreference functionPref;
    TextView tvStatus;
    TextView tvTimer1;
    //TextView tvTimer2;
    Button btnStart1;
    //Button btnStart2;
    Button btnStop;
    Button btnStop1;
    // Button btnStop2;
    Button btnReset;
    ToggleButton togglebtnUpdate;
    Button btnUpdate;
    long startTime1 = 0;
    long startTime2 = 0;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    boolean flag = false;
    boolean flag2 = false;

    Handler mapHandler = new Handler();
    Runnable mapRunnable = new Runnable() {
        public void run() {
            //btnUpdate.performClick();
            onBtnUpdatePressed(null);

            mapHandler.postDelayed(this, 5000);
        }
    };

    Handler timerHandler1 = new Handler();
    Runnable timerRunnable1 = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime1;
            int seconds = (int) (millis / 1000.0);
            //int minutes = seconds / 60;
            seconds = seconds % 60;

            tvTimer1.setText(String.format("%d:%d", ((int)seconds), ((long)millis%100)));

            timerHandler1.postDelayed(this, 0);
        }
    };

    Handler timerHandler2 = new Handler();
    Runnable timerRunnable2 = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime2;
            int seconds = (int) (millis / 1000.0);
            //int minutes = seconds / 60;
            seconds = seconds % 60;

            // tvTimer2.setText(String.format("%d:%d", ((int)seconds), ((long)millis%100)));

            timerHandler2.postDelayed(this, 0);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mSensorManager = (SensorManager) getSystemService(MainActivity.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BC = (BluetoothConnect) getApplication();


        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, 1);

        pgv = (PixelGridView) findViewById(R.id.pixelGridView);

        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvTimer1 = (TextView) findViewById(R.id.tv_timer1);
        //tvTimer2 = (TextView) findViewById(R.id.tv_timer2);

        btnStart1 = (Button) findViewById(R.id.btn_start1);
        //btnStart2 = (Button) findViewById(R.id.btn_start2);
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop1 = (Button) findViewById(R.id.btn_stop1);
        //btnStop2 = (Button) findViewById(R.id.btn_stop2);
        btnReset = (Button) findViewById(R.id.btn_reset);
        togglebtnUpdate = (ToggleButton) findViewById(R.id.togglebtn_update);

        btnUpdate = (Button) findViewById(R.id.btn_update);

        functionPref = new FunctionPreference(getApplicationContext());
        btnStop1.setEnabled(false);
        startbluetooth();

    }
    public void startbluetooth() {
        adapter.stopLeScan(callback);
        adapter.startLeScan(callback);
    }

    BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            System.out.println("findDevice");
            System.out.println("name:" + device.getName());
            int connectState = device.getBondState();
            switch (connectState) {
                case BluetoothDevice.BOND_NONE:
                    // if not bonded, then bond
                    try {
                        Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                        createBondMethod.invoke(device);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                // if bonded
                case BluetoothDevice.BOND_BONDED:
                    try {
                        // connect
                        System.out.println("Can connect");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth_connect:
                Intent bluetoothConnectIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(bluetoothConnectIntent, 1);
                return true;
            case R.id.bluetooth_chat:
                Intent bluetoothChatIntent = new Intent(this, BluetoothChat.class);
                startActivity(bluetoothChatIntent);
                return true;
            case R.id.buttons_config:
                Intent functionButtonsConfig = new Intent(this, PreferenceActivity.class);
                startActivity(functionButtonsConfig);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) //setupChat();
            {
                mChatService = new BluetoothChatService(this, mHandler);
            }
        }
    }

     @Override
     public synchronized void onResume() {
         super.onResume();

         if(D) Log.e(TAG, "+ ON RESUME +");

         // Performing this check in onResume() covers the case in which BT was
         // not enabled during onStart(), so we were paused to enable it...
         // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
         if (mChatService != null) {
             // Only if the state is STATE_NONE, do we know that we haven't started already
             if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                 // Start the Bluetooth chat services
                 mChatService.start();
             }
         }

         if (BC.getBluetoothConnectedThread() != null) {
             //mHandler = BC.getHandler();
             mChatService = BC.getBluetoothConnectedThread();
             //mHandler = BC.getHandler();
             mChatService.setHandler(mHandler);
             mHandler = BC.getHandler();
         }

         mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
     }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();

        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }


    private void setStatus(int resId) {

        try{
            final ActionBar actionBar = getActionBar();
            actionBar.setSubtitle(resId);
        }
        catch(NullPointerException e){};
    }

    private void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }

    // The Handler that gets information back from the BluetoothChatService
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            BC.setDeviceName(mConnectedDeviceName);
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (readMessage.contains("GRID=")) {
                        String colRow = readMessage.substring(readMessage.indexOf("=")+1, readMessage.indexOf("|"));
                        //System.out.println("colrow " +colRow);
                        //System.out.println("row " + colRow.substring(colRow.indexOf("?"), colRow.length()));
                        int col = Integer.parseInt(colRow.substring(0, colRow.indexOf("?")));
                        int row = Integer.parseInt(colRow.substring(colRow.indexOf("?")+1, colRow.length()));

                        String direction = readMessage.substring(readMessage.indexOf("|")+1, readMessage.lastIndexOf("|"));

                        String map = readMessage.substring(readMessage.lastIndexOf("|")+1, readMessage.length());
                        System.out.println("map = " + map);

                        pgv.mapInString(map, col, row, direction);
                    }

                    if(readMessage.contains("GRID 5")) {
                        int row = Integer.parseInt(readMessage.substring(readMessage.indexOf("5"), readMessage.indexOf("5")+1));

                        if(readMessage.substring(readMessage.indexOf("5")+2, readMessage.indexOf("5")+3).equals("5")) {
                            int column = Integer.parseInt(readMessage.substring(readMessage.indexOf("5")+2, readMessage.indexOf("5")+3));
                            pgv.setNumColumns(column);
                            pgv.setNumRows(row);

                            String map = readMessage.substring(readMessage.indexOf("5")+12, readMessage.length());
                            pgv.mapInStringAMD(map, column, row);
                        } else {
                            int column = Integer.parseInt(readMessage.substring(readMessage.indexOf("5")+2, readMessage.indexOf("5")+4));
                            pgv.setNumColumns(column);
                            pgv.setNumRows(row);
                            String map = readMessage.substring(readMessage.indexOf("5")+13, readMessage.length());
                            pgv.mapInStringAMD(map, column, row);
                        }
                    } else if (readMessage.contains("GRID 10") ||
                            readMessage.contains("GRID 15") ||
                            readMessage.contains("GRID 20") ||
                            readMessage.contains("GRID 25") ||
                            readMessage.contains("GRID 30")) {
                        int row = Integer.parseInt(readMessage.substring(5, 7));

                        if(readMessage.substring(8,9).equals("5")) {
                            int column = Integer.parseInt(readMessage.substring(8,9));
                            pgv.setNumColumns(column);
                            pgv.setNumRows(row);
                            String map = readMessage.substring(readMessage.indexOf("5")+13, readMessage.length());
                            pgv.mapInStringAMD(map, column, row);
                        } else {
                            int column = Integer.parseInt(readMessage.substring(8, 10));
                            pgv.setNumColumns(column);
                            pgv.setNumRows(row);
                            String map = readMessage.substring(18);

                            pgv.mapInStringAMD(map, column, row);
                        }
                    }

                    if (readMessage.contains("moving")) {
                        tvStatus.setText("Moving");
                    } else if (readMessage.contains("stop")) {
                        tvStatus.setText("Stop");
                    } else if (readMessage.contains("left")) {
                        tvStatus.setText("Turning Left");
                    } else if (readMessage.contains("right")) {
                        tvStatus.setText("Turning Right");
                    }


                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //setupChat();
                    mChatService = new BluetoothChatService(this, mHandler);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);


        System.out.println("BLUETOOTHCONNECT " + BC);
        if (BC.getBluetoothConnectedThread() == null) {
            BC.setBluetoothConnectedThread(mChatService, mHandler);

            System.out.println("BLUETOOTHCONNECT " + "entered ");
        } else {
            System.out.println("BLUETOOTHCONNECT " + "problem");
        }
    }


    public void robopos(View v)
    {
        xpostext=(EditText)findViewById(R.id.xpos);
        // System.out.println(xpostext);

        ypostext=(EditText)findViewById(R.id.ypos);
        possend = (Button) findViewById(R.id.button);
        possend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(xpostext.getText().toString());
                sendMessage(ypostext.getText().toString());
                try{
                    Integer x=Integer.parseInt(xpostext.getText().toString()), y=Integer.parseInt(ypostext.getText().toString());
                    pgv.setCoordinates(x,y);
                }
                catch(Exception e){
                    System.out.printf("x or y you input is not integer.");
                }
            }});
    }


    public void onBtnLeftPressed(View view) {
        pgv.moveLeft();
        //actual robot
        //sendMessage("a2");
        //AMD test
        sendMessage("a");
        //Toast.makeText(this,"Turning Left", Toast.LENGTH_SHORT).show();
        final Toast toast = Toast.makeText(this,"Turning Left", Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    public void onBtnForwardPressed(View view) {
        pgv.moveForward();
        //actual robot
        //sendMessage("a1");
        //AMD test
        sendMessage("w");
        final Toast toast = Toast.makeText(this,"Moving Down", Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    public void onBtnRightPressed(View view) {
        pgv.moveRight();
        //actual robot
        //sendMessage("a3");
        //AMD test
        sendMessage("d");
        final Toast toast = Toast.makeText(this,"Turning Right", Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    //for AMD testing > this is down button
    public void onBtnStopPressed(View view) {
        //actual robot
        //sendMessage("p0");
        //AMD test
        pgv.moveDown();
        sendMessage("s");
        final Toast toast = Toast.makeText(this,"Moving Up", Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    public void onBtnF1Pressed(View view) {
        String function_pref_string_f1 = functionPref.getFunctionsDetails().get("f1");
        sendMessage("a" + function_pref_string_f1);
    }

    public void onBtnF2Pressed(View view) {
        String function_pref_string_f2 = functionPref.getFunctionsDetails().get("f2");
        sendMessage("a" + function_pref_string_f2);
    }

    public void onBtnStart1Pressed(View view) {
        sendMessage("pstart:e");
        startTime1 = System.currentTimeMillis();
        timerHandler1.postDelayed(timerRunnable1, 0);
        btnStart1.setEnabled(false);
        btnStop1.setEnabled(true);
    }

    public void onBtnStop1Pressed(View view) {
        timerHandler1.removeCallbacks(timerRunnable1);
        sendMessage("p0");
        btnStart1.setEnabled(true);
        btnStop1.setEnabled(false);
    }

    public void onBtnResetPressed(View view) {
        tvTimer1.setText(R.string.timer_default);
        //tvTimer2.setText(R.string.timer_default);
    }

    public void onTogglebtnUpdatePressed(View view) {
        if(togglebtnUpdate.isChecked()) {
            //isChecked == true == auto
            btnUpdate.setEnabled(false);

            mapHandler.post(mapRunnable);
        } else {
            //isChecked == false == manual
            btnUpdate.setEnabled(true);
            mapHandler.removeCallbacks(mapRunnable);

        }
    }

    public void onBtnUpdatePressed(View view) {
        sendMessage("GRID");
        //request map from rpi/pc
    }


    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float z1 = event.values[2];
        float x1 = event.values[0];
        float y1 = event.values[1];

        int x = (int)(x1*1000);
        int y = (int)(y1*1000);
        int z = (int)(z1*1000);

        if (x > 5000) {
            //left
            if (flag == true) {
                //onBtnLeftPressed(null);
                pgv.moveLeft();
                flag = false;
            }
        } else if (x < -5000) {
            //right
            if (flag == true) {
                //onBtnRightPressed(null);
                pgv.moveRight();
                flag = false;
            }
        } else {
            flag = true;
        }


        if (y < 3000 ) {
            if(flag2 == true) {
                //onBtnForwardPressed(null);
                pgv.moveDown();
                flag2 = false;
            }
        } else if (x < -3000) {
            if (flag2 == true) {
                pgv.moveForward();
                flag2 = false;
            }
        }


        flag2 = true;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        // important to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}