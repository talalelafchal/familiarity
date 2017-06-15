public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener{

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10 * 1000;
    private static final String TAG = "MainActivityTAG_";

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothLeScanner mBluetoothScanner;
    private ArrayAdapter<String> mArrayAdapter;

    private ArrayList<BluetoothDevice> mBluetoothDevices;

    @Bind(R.id.list_view_1)
    ListView mListView;

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, "onScanResult: " + result.toString());
            BluetoothDevice bluetoothDevice = result.getDevice();
            mArrayAdapter.add(bluetoothDevice.getAddress() + " " + bluetoothDevice.getName());
            mBluetoothDevices.add(bluetoothDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d(TAG, "onBatchScanResults: " + results.toString());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "onScanFailed: " + errorCode);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mHandler = new Handler();
        mBluetoothDevices = new ArrayList<>();

        checkIfBleSupported();
        initializeBluetoothManager();
        checkIfBluetoothEnabled();

        setListView();

        scanLeDevice(true);
    }

    private void setListView() {
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(this);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothScanner.stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mBluetoothScanner.startScan(mLeScanCallback);
        } else {
            mBluetoothScanner.startScan(mLeScanCallback);
        }
    }

    private void checkIfBluetoothEnabled() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void initializeBluetoothManager() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    private void checkIfBleSupported() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DeviceActivity.class);

        intent.putExtra(DeviceActivity.EXTRA_DEVICE_KEY, mBluetoothDevices.get(position));

        startActivity(intent);
    }
}
