package epicsyst.com.beacontest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothLeScanner;
    Button btScan, btStopScan;
    ScanCallback scanCallback;
    ScanFilter scanFilter;
    ScanSettings scanSettings;
    ArrayList<BluetoothDevice> devices;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(MainActivity.this.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        btScan = (Button) findViewById(R.id.btScan);
        btStopScan = (Button) findViewById(R.id.btStopScan);

        askForPer();
        devices = new ArrayList<>();

        btStopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothLeScanner.stopScan(scanCallback);
            }
        });


        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBluetoothLeScanner.startScan(new ScanCallback() {
                        @Override
                        public void onScanResult(int callbackType, ScanResult result) {
                            super.onScanResult(callbackType, result);
                            Log.i("res ", result.toString());
                            if (result.getDevice().fetchUuidsWithSdp()) {
                                Log.i("loc fetch", "true ");
                                ParcelUuid[] parcelUuid = result.getDevice().getUuids();
                                System.out.print("size "+parcelUuid.length);
                                Log.i("dev name", result.getDevice().getName());

                            } else {
                                Log.d("Error","Didnt detect any UUIDs");
                            }

                            byte[] scanRecord = result.getScanRecord().getBytes();
                            String uuid2 = IntToHex2(scanRecord[9] & 0xff) + IntToHex2(scanRecord[10] & 0xff) + IntToHex2(scanRecord[11] & 0xff) + IntToHex2(scanRecord[12] & 0xff)
                                    + "-" + IntToHex2(scanRecord[13] & 0xff) + IntToHex2(scanRecord[14] & 0xff)
                                    + "-" + IntToHex2(scanRecord[15] & 0xff) + IntToHex2(scanRecord[16] & 0xff)
                                    + "-" + IntToHex2(scanRecord[17] & 0xff) + IntToHex2(scanRecord[18] & 0xff)
                                    + "-" + IntToHex2(scanRecord[19] & 0xff) + IntToHex2(scanRecord[20] & 0xff) + IntToHex2(scanRecord[21] & 0xff) + IntToHex2(scanRecord[22] & 0xff) + IntToHex2(scanRecord[23] & 0xff) + IntToHex2(scanRecord[24] & 0xff);
                            System.out.print("UUID2 "+uuid2);
                        }

                        @Override
                        public void onBatchScanResults(List<ScanResult> results) {
                            super.onBatchScanResults(results);
                            for (int i = 0; i < results.size(); i++) {
                                devices.add(results.get(i).getDevice());
                                System.out.print("devices "+devices.get(i));
                            }
                        }

                        @Override
                        public void onScanFailed(int errorCode) {
                            super.onScanFailed(errorCode);
                            Log.i("loc error", "" + errorCode);
                        }
                    });
                }
            }
        });
    }


    public String IntToHex2(int i) {
        char hex_2[] = {Character.forDigit((i >> 4) & 0x0f, 16), Character.forDigit(i & 0x0f, 16)};
        String hex_2_str = new String(hex_2);
        return hex_2_str.toUpperCase();

    }

    private void askForPer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

}
