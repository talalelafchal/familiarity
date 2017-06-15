package com.st.modesetting;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by ly on 11/6/16.
 */
public class BtPanStateReceiver extends BroadcastReceiver {
    public static class BtPanService extends IntentService {
        private static final int CONNECT_PROXY_TIMEOUT = 5000;
        private BluetoothAdapter mAdapter = null;
        private BluetoothPan mPan = null;
        private BluetoothProfile.ServiceListener mServiceListener =
                new BluetoothProfile.ServiceListener() {
                    @Override
                    public void onServiceConnected(int profile, BluetoothProfile proxy) {
                        synchronized (this) {
                            if (profile == BluetoothProfile.PAN)
                                mPan = (BluetoothPan) proxy;
                        }
                    }

                    @Override
                    public void onServiceDisconnected(int i) {
                        mPan = null;
                    }
                };

        public BtPanService() {
            super("BtPanStateReceiver$BtPanService");
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.d("[LY]", "OnCreate");
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mAdapter == null)
                return;
            mAdapter.getProfileProxy(this, mServiceListener, BluetoothProfile.PAN);
        }

        @Override
        public void onDestroy() {
            Log.d("[LY]", "OnDestroy");
            mAdapter.closeProfileProxy(BluetoothProfile.PAN, mPan);
            super.onDestroy();
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            Log.d("[LY]", "OnHandleIntent");
            long s = System.currentTimeMillis();
            while (mPan == null && System.currentTimeMillis() - s < CONNECT_PROXY_TIMEOUT) {
                SystemClock.sleep(100);
            }

            int state = intent.getIntExtra(
                    BluetoothPan.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (!device.getName().startsWith("SIDEBRIDGE_")) {
                Log.d("[LY]", "TODO: Gateway is not SIDEBRIDGE");
                stopService(intent);
                return;
            };

            switch (state) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    // Try to reconnect back
                    Log.d("[LY]", "Disconnected from " + device.getName() + ", try to connect back");
                    int current_state = mPan.getConnectionState(device);
                    if (current_state == BluetoothPan.STATE_DISCONNECTED ||
                            current_state == BluetoothPan.STATE_DISCONNECTING)
                        mPan.connect(device);
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    // TODO: Connected, reset the failure count?
                    Log.d("[LY]", "Connected to " + device.getName());
                    break;
            }
            stopService(intent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, BtPanService.class);
        i.putExtras(intent.getExtras());
        context.startService(i);
    }
}
