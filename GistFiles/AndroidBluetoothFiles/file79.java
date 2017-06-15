package com.dt.cstag.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.annotation.Keep;
import android.text.TextUtils;
import com.dt.cstag.CsTagAdvertiser;
import com.dt.cstag.CsTagUiHandler;
import com.dt.cstag.CsTagUiHandlerImpl;
import java.util.UUID;
import rx.Observable;
import rx.Subscriber;

/**
 * Scanner adapter implementation.
 */
@Keep
public class CsTagBluetoothScannerAdapter implements ICsTagBluetoothAdapter {
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private Context mContext;
    private boolean isFiltered;
    public static final String NAME = "CS_TAG";
    private static CsTagUiHandler handler = new CsTagUiHandlerImpl();

    public CsTagBluetoothScannerAdapter (Context context) {
        this.mContext = context;
    }

    /**
     * Returnes rx observable of the scanning process.
     * @return
     */
    public Observable<CsTagAdvertiser> getObservable () {
        return Observable.create(new Observable.OnSubscribe<CsTagAdvertiser>() {
            @Override
            public void call (Subscriber<? super CsTagAdvertiser> subscriber) {
                BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                leScanCallback = (device, rssi, scanRecord) -> {
                    handler.post(() -> {
                        String name = device.getName();
                        if (TextUtils.isEmpty(name)) {
                            return;
                        }
                        if (isFiltered && !name.toLowerCase().contains(NAME.toLowerCase())) {
                            return;
                        }
                        subscriber.onNext(new CsTagAdvertiser(device, rssi, scanRecord));
                    });
                };
                bluetoothAdapter.startLeScan(leScanCallback);
            }
        }).doOnUnsubscribe(() -> {
            BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            bluetoothAdapter.stopLeScan(leScanCallback);
            leScanCallback = null;
        });
    }

    @Override
    public BluetoothDevice getRemoteDevice (String address) {
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter.getRemoteDevice(address);
    }

    @Override
    public BluetoothDevice getRemoteDevice (byte[] address) {
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter.getRemoteDevice(address);
    }

    @Override
    public ICsTagBluetoothAdapter setIsFiltered (boolean isFiltered) {
        this.isFiltered = isFiltered;
        return this;
    }
}