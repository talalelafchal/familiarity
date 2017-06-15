package gclue.com.myble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MyActivity extends Activity {

    /** Bluetooth Adapter. */
    private static BluetoothAdapter mBluetoothAdapter;

    /** Handler(BLEスキャン用). */
    private Handler mHandler;

    /** BLEのScan時間. */
    private static final long SCAN_PERIOD = 100;

    /** BLEのScan周期. */
    private static final long SCAN_SLEEP_TIME = 1000;

    /** Scan状態かどうか. */
    private boolean mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // Get BLE Adapter
        BluetoothManager mBluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.i("BLE_SAMPLE", "not available BLE");
        } else {
            Log.i("BLE_SAMPLE", "available BLE");
        }

        mHandler = new Handler();

        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScanning = false;
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        }
                    }, SCAN_PERIOD);

                    mScanning = true;
                    mBluetoothAdapter.startLeScan(mLeScanCallback);

                    try {
                        Thread.sleep(SCAN_SLEEP_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    /**
     * BLEのScan.
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            Log.i("BLE_SAMPLE", "device: " + device.getAddress() + " - " + device.getName());
            Log.i("BLE_SAMPLE", "rssi: " + rssi);
            StringBuilder advertisingString = new StringBuilder();
            for (int b : scanRecord) {
                advertisingString.append(Integer.toHexString(b >> 4 & 0xF));
                advertisingString.append(Integer.toHexString(b & 0xF));
            }
            Log.i("BLE_SAMPLE", "scanRecord: " + advertisingString);
        }
    };

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
