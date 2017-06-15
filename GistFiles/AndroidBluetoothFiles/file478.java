package gclue.com.mybrain;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;
import com.neurosky.thinkgear.TGRawMulti;

public class MyActivity extends Activity {

    /** Adapter of bluetooth. */
    private BluetoothAdapter bluetoothAdapter;

    /** TGDevice. */
    private TGDevice tgDevice;

    /** TGEegPower. */
    private TGEegPower fbands;

    /** Raw Dataをとるか. */
    private final boolean rawEnabled = true;

    /** TAG. */
    private final static String TAG = "BRAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            // Alert user that Bluetooth is not available
            finish();
            return;
        }else {
        	/* create the TGDevice */
            tgDevice = new TGDevice(bluetoothAdapter, handler);
            doStuff();
        }
    }

    /**
     * Handles messages from TGDevice
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TGDevice.MSG_STATE_CHANGE:

                    switch (msg.arg1) {
                        case TGDevice.STATE_IDLE:
                            break;
                        case TGDevice.STATE_CONNECTING:
                            Log.d(TAG, "Connecting...");
                            break;
                        case TGDevice.STATE_CONNECTED:
                            Log.d(TAG, "Connected.");
                            tgDevice.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                            Log.d(TAG, "Can't find.");
                            break;
                        case TGDevice.STATE_NOT_PAIRED:
                            Log.d(TAG, "not paired.");
                            break;
                        case TGDevice.STATE_DISCONNECTED:
                            Log.d(TAG, "Disconnected mang.");
                    }

                    break;
                case TGDevice.MSG_EEG_POWER:
                    Log.d("TAG","EEG");

                    fbands = (TGEegPower)msg.obj;
                    Log.d(TAG, "Delta:" + fbands.delta);
                    Log.d(TAG, "Theta:" + fbands.theta);
                    Log.d(TAG, "High Alpha:" + fbands.highAlpha);
                    Log.d(TAG, "High Beta:" + fbands.highBeta);
                    Log.d(TAG, "High Gamma:" +  fbands.midGamma);
                    Log.d(TAG, "Low Alpha:" +  fbands.lowAlpha);
                    Log.d(TAG, "Low Beta:" +  fbands.lowBeta);
                    Log.d(TAG, "Low Gamma:" +  fbands.lowGamma);

                    break;

                case TGDevice.MSG_POOR_SIGNAL:
                    Log.d(TAG, "PoorSignal:" + msg.arg1);
                    break;
                case TGDevice.MSG_RAW_DATA:
                    //Log.d(TAG, "Got raw: " + msg.arg1);
                    break;
                case TGDevice.MSG_HEART_RATE:
                    Log.d(TAG, "Heart rate: " + msg.arg1);
                    break;
                case TGDevice.MSG_ATTENTION:
                    Log.d(TAG, "Attention: " + msg.arg1);
                    break;
                case TGDevice.MSG_MEDITATION:
                    Log.d(TAG, "Meditation: " + msg.arg1);
                    break;
                case TGDevice.MSG_BLINK:
                    Log.d(TAG, "Blink: " + msg.arg1 );
                    break;
                case TGDevice.MSG_RAW_COUNT:
                    Log.d(TAG, "Raw Count: " + msg.arg1);
                    break;
                case TGDevice.MSG_LOW_BATTERY:
                    Log.d(TAG, "Low battery");
                    break;
                case TGDevice.MSG_RAW_MULTI:
                    TGRawMulti rawM = (TGRawMulti)msg.obj;
                    Log.d(TAG, "Raw1: " + rawM.ch1 + " Raw2: " + rawM.ch2);
                    break;
                default:
                    break;
            }
        }
    };

    public void doStuff() {
        if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
            tgDevice.connect(rawEnabled);
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
