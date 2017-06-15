package net.iseteki.example.konashiRAZRSample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.IBluetoothGattProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import com.motorola.bluetoothle.BluetoothGatt;

@SuppressWarnings("unused")
public class KonashiManager {

    public static final String ACTION_CONNECT_COMPLETED = "KonashiManager_ConnectCompleted";
    public static final String ACTION_VALUE_CHANGED = "KonashiManager_ValueChanged";
    public static final String EXTRA_VALUE = "Value";

    private static final String GATT_SERVICE_CLASS_NAME = "android.bluetooth.BluetoothGattService";

    private static final int NOT_CHECKED = 0;
    private static final int NO_SUPPORTED = 1;
    private static final int SUPPORTED = 2;

    private static final ParcelUuid KONASHI_SERVICE_UUID = ParcelUuid.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
    private static final ParcelUuid PIO_INPUT_VALUE_STATE_UUID = ParcelUuid.fromString("00003003-0000-1000-8000-00805f9b34fb");

    private static int sBluetoothLESupportFlag = NOT_CHECKED;

    private BluetoothAdapter mBluetoothAdapter;

    private Context mContext;
    private FoundReceiver mFoundReceiver;

    BluetoothDevice mDevice;
    BluetoothGattService mService;
    String mObjectPath;

    //<editor-fold desc="Static Methods">

    /**
     * この端末でBluetooth LEがサポートされるか確認する
     * @return サポートされている場合はtrue
     */
    public static boolean isBluetoothLESupported() {
        if (sBluetoothLESupportFlag == NOT_CHECKED) {
            try
            {
                Class.forName(GATT_SERVICE_CLASS_NAME);
                sBluetoothLESupportFlag = SUPPORTED;

            } catch (Exception e) {
                sBluetoothLESupportFlag = NO_SUPPORTED;
            }

        }
        return (sBluetoothLESupportFlag == SUPPORTED);
    }

    //</editor-fold>

    public KonashiManager() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // TODO: 例外投げる
        }
    }

    public void startDiscovery() {

        // #1. ACTION_FOUND(デバイス発見) / ACTION_GATT(GATT Service発見) を受信する
        // BroadcastReceiver を作る
        mFoundReceiver = new FoundReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_GATT);
        getContext().registerReceiver(mFoundReceiver, filter);

        // #2. デバイスを検索する
        mBluetoothAdapter.startDiscovery();
    }

    public void cancelDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if (mFoundReceiver != null) {
            mContext.unregisterReceiver(mFoundReceiver);
            mFoundReceiver = null;
        }
    }

    private void onFoundDevice(BluetoothDevice device, String objectPath) {
        Log.d("BLE", "Found konashi.");
        if (mDevice == null) {
            connect(device, objectPath);
        }
    }

    public void connect(BluetoothDevice device, String objectPath) {
        Log.d("BLE", "Connect");
        mObjectPath = objectPath;
        mDevice = device;
        // #5. Profile を指定して Service のインスタンスを作成する。
        //     インスタンスを作成すると、自動的にCharacterisitcs の列挙が始まる。
        //     InteliJ ではこの行にエラーが出るけど普通にビルド通るので無視してOK
        mService = new BluetoothGattService(mDevice, KONASHI_SERVICE_UUID, mObjectPath, new KonashiProfile());
        try {
            // #6. Notify受信のコールバックを有効にする
            mService.registerWatcher();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (mDevice != null) {
            try {
                mService.close();
                mService = null;
                mDevice = null;
                mObjectPath = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setContext(Context context) {
        if (mContext != null) {
            cancelDiscovery();
            mContext = null;
        }
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.enable();
    }

    private class FoundReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = (BluetoothDevice) intent.getExtras().get(BluetoothDevice.EXTRA_DEVICE);

            String action = intent.getAction();
            Log.d("BLE", "FoundReceiver: "+action);

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                // #3. デバイスが見つかったら、そのデバイスに対してGATT Serviceを要求する
                device.getGattServices(KONASHI_SERVICE_UUID.getUuid());
            }
            else if (action.equals(BluetoothDevice.ACTION_GATT)) {
                // #4. GATT Serviceが見つかったら、Characteristic の Object Path を取得する。
                //     Motorola BLE API はService/Characteristics を UUID では直接処理できず、
                //     Object Path との相互変換が必要。
                String[] pathArray = intent.getStringArrayExtra(BluetoothDevice.EXTRA_GATT);
                if (pathArray != null && pathArray.length > 0) {
                    onFoundDevice(device, pathArray[0]);
                }
            }
        }
    }

    private class KonashiProfile extends IBluetoothGattProfile.Stub {

        @Override
        public void onDiscoverCharacteristicsResult(String path, boolean result) throws RemoteException {
            Log.d("BLE", "onDiscoverCharacteristicsResult path: " + path + " / result:" + (result ? "T" : "F"));

            // #7. Characteristic の検索が完了するとこのメソッドが呼ばれる

            for (String c : mService.getCharacteristics()) {
                Log.d("BLE", "Chara: " + c);
                // #8. Characteristic は Object Path の形式で渡されるため、
                //     UUID との比較のため、getCharacteristicUuid を実行して比較する。
                //     今回は UUID_PIO_INPUT_VALUE_STATE を監視して、PIO 0 のタクトスイッチを監視します
                if (mService.getCharacteristicUuid(c).getUuid().equals(PIO_INPUT_VALUE_STATE_UUID.getUuid())) {
                    try {
                        Log.d("BLE", "setCharacteristicClientConf: " + c);
                        // #9. Notify を有効にする (なんでこれ定数切ってないんだろう...)
                        mService.setCharacteristicClientConf(c, ((1 << 8) & 0xff00));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Intent i = new Intent(ACTION_CONNECT_COMPLETED);
            getContext().sendBroadcast(i);
        }

        @Override
        public void onSetCharacteristicValueResult(String path, boolean result) throws RemoteException {
            Log.d("BLE", "onSetCharacteristicValueResult path: " + path + " / result:" + (result ? "T" : "F"));

            // Characteristic への値の書き込みができた場合に呼ばれる
        }

        @Override
        public void onSetCharacteristicCliConfResult(String path, boolean result) throws RemoteException {
            Log.d("BLE", "onSetCharacteristicCliConfResult path: " + path + " / result:" + (result ? "T" : "F"));

            // Characteristic への設定変更ができた場合に呼ばれる
        }

        @Override
        public void onUpdateCharacteristicValueResult(String path, boolean result) throws RemoteException {
            Log.d("BLE", "onUpdateCharacteristicValueResult path: " + path + " / result:" + (result ? "T" : "F"));

            // Characteristic の値を受信した場合に呼ばれる
        }

        @Override
        public void onValueChanged(String path, String value) throws RemoteException {
            Log.d("BLE", "onValueChanged path: " + path + " / value:" + value);

            // #10. 値変更のNotify が飛んでくるとこのメソッドが呼ばれる

            Intent i = new Intent(ACTION_VALUE_CHANGED);
            i.putExtra(EXTRA_VALUE, value);
            getContext().sendBroadcast(i);
        }
    }
}
