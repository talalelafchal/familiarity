package andrej.jelic.attend;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final boolean D = true;
    public static final String PREFS_NAME = "PrefsFile";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    SharedPreferences prefs;
    private ProgressBar mProgressBar;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    private String name;
    private String lastName;
    private String indexNumber;
    private String adresa;
    private String uuid;
    private String option;
    private String attendText;
    private String leaveText;
    private String mConnectedDeviceName = null;
    private String error;

    private boolean hasLoggedIn;
    private boolean reconnect;
    private boolean firstCall;
    private boolean connected = false;

    private TextView ime_studenta;
    private TextView prezime_studenta;
    private TextView broj_indexa;
    private TextView mTitle;
    private Button attend;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        error = getString(R.string.Error);

        checkLogin();
        attendText = getResources().getString(R.string.Attend);
        leaveText = getResources().getString(R.string.Leave);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_main);
        ime_studenta = (TextView) findViewById(R.id.name_text_main);
        prezime_studenta = (TextView) findViewById(R.id.last_name_text_main);
        broj_indexa = (TextView) findViewById(R.id.index_number_main);

        attend = (Button) findViewById(R.id.buttonAttend);
        mTitle = (TextView) findViewById(R.id.state_title);

    }

    private void checkLogin() {
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        hasLoggedIn = prefs.getBoolean("hasLoggedIn", false);

        if (!hasLoggedIn) {
            Intent login = new Intent(Intent.ACTION_VIEW);
            login.setClass(this, LoginActivity.class);
            startActivity(login);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "On start ");
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
        loadData();
        Log.e(TAG, "On resume ");

        if (!mBluetoothAdapter.isEnabled()) {
            new Enable().execute();
            Log.e(TAG, "Enable blutut ");
        } else if (mChatService == null) {
            Log.e(TAG, "Setup chat jer je nula ");
            setupChat();
            makeConnection();
        } else makeConnection();

        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check that we're actually connected before trying anything
                if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                    Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e(TAG, "Option u onResume" + option);
                String message = option + " " + name + " " + lastName + " " + indexNumber;
                Log.e(TAG, "Message " + message);
                sendMessage(message);
            }
        });
    }

    private void init() {
        editor = prefs.edit();
        firstCall = prefs.getBoolean("firstCall", false);
        Log.e(TAG, "first call " + firstCall);
        reconnect = prefs.getBoolean("reconnect", false);
        Log.e(TAG, "reconnect " + reconnect);

        if (!firstCall && !reconnect) {
            editor.putString("Adresa", "default");

        }
        editor.putBoolean("firstCall", false);
        editor.apply();
    }

    private void loadData() {

        option = prefs.getString("Option", attendText);
        name = prefs.getString("Name", "noName");
        lastName = prefs.getString("LastName", "noLastName");
        indexNumber = prefs.getString("IndexNumber", "noIndexNumber");

        adresa = prefs.getString("Adresa", "default");


        Log.e(TAG, "My name " + name + "\nLast name " + lastName + "\nIndex " + indexNumber);

        ime_studenta.setText(name);
        prezime_studenta.setText(lastName);
        broj_indexa.setText(indexNumber);
        attend.setText(option);

        Log.e(TAG, "get remote devicee " + adresa);

    }

    private void makeConnection() {

            if (!adresa.equals("default")) {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(adresa);
                mChatService.connect(device);

            }

    }

    private void setupChat() {
        Log.e(TAG, "setupChat()");
        mChatService = new BluetoothChatService(this, mHandler);
        mChatService.stop();
    }


    private void sendMessage(String message) {

        byte[] send = message.getBytes();
        mChatService.write(send);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.e(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                          //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                            mTitle.setText(R.string.title_connected_to);
                            mTitle.append(mConnectedDeviceName);
                            connected = true;
                            invalidateOptionsMenu();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            mTitle.setText(R.string.listening);
                            break;
                        case BluetoothChatService.STATE_NONE:
                            connected = false;
                            invalidateOptionsMenu();
                          //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            mTitle.setText(R.string.not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    Log.e(TAG, "mmOutStream write" + writeBuf);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.e(TAG, "Message incoming " + readMessage);
                    editor = prefs.edit();
                    if (readMessage.length() > 0) {
                        if (readMessage.contains(attendText)) {

                            attend.setText(R.string.Leave);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.attending), Toast.LENGTH_LONG).show();
                            editor.putString("Option", leaveText);
                            editor.putBoolean("reconnect", true);
                        }
                        else if (readMessage.contains(leaveText)) {

                            attend.setText(R.string.Attend);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.leave), Toast.LENGTH_LONG).show();
                            editor.putString("Option", attendText);
                            editor.putBoolean("reconnect", false);
                        } else if (readMessage.contains(error)) {

                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.forgot), Toast.LENGTH_LONG).show();
                            attend.setText(R.string.Attend);
                            editor.putString("Option", attendText);
                            editor.putBoolean("reconnect", false);
                        }
                        Log.e(TAG, "Read message " + readMessage);
                        editor.apply();
                        mChatService.stop();
                        // mBluetoothAdapter.disable();

                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem scan = menu.findItem(R.id.scan);
        MenuItem disconnect = menu.findItem(R.id.disconnect);

        if (mBluetoothAdapter.isEnabled()) scan.setEnabled(true);

        if (connected) disconnect.setEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.scan:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivity(serverIntent);
                return true;

            case R.id.disconnect:
                mChatService.stop();

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public class Enable extends AsyncTask<String, Integer, String> {

        private static final String TAG = "Enabling bluetooth ";
        private String enabledBluetooth;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            enabledBluetooth = getResources().getString(R.string.bt_enabled);

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e(TAG, "Enable do in background");
            mBluetoothAdapter.enable();

            for (int i = 1; i < 100; i++) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress(i);
            }


            return enabledBluetooth;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                invalidateOptionsMenu();
                setupChat();
                makeConnection();
            } else {
                Log.e(TAG, "Bluetooth not enabled");
                Toast.makeText(getApplicationContext(), R.string.bluetooth_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        boolean changing = this.isChangingConfigurations();
        if (!changing) {
            //mBluetoothAdapter.disable();

            Log.e(TAG, "Zavrsi ako nije promjena konfiguracije");
        }
    }

}
