
//connects and reads from usb device
public class PollUSBActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "USBReader";

    private TextView mLog;

    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;
    public UsbEndpoint mEndpointOut;
    public UsbEndpoint mEndpointIn;
    private boolean startedScan = false;
    private boolean finishedScan = false;
    private String lastDataReceived = "";


    private Button btnClear;

    private boolean disableUSBThread = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLog = (TextView) findViewById(R.id.main_info);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        btnClear.setVisibility(View.INVISIBLE);

    }

    public void onClick(View v) {
        if (v == btnClear) {
            mLog.setText("");
            errorLog.setText("");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        disableUSBThread = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();

        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        if (deviceList.size() > 0) {
            disableUSBThread = false;
            setDevice((UsbDevice) deviceList.values().toArray()[0]);
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            if (mDevice != null && mDevice.equals((UsbDevice) deviceList.values().toArray()[0])) {
                mConnection.close();
                mLog.setText("Device detached\n");
                disableUSBThread = true;
            }
        } else {
            mLog.setText("\nNo device connected\n");
        }
    }

    public void parseUsb() {
        if (!disableUSBThread) {
            new UsbTicketInfo(this).execute("a", "b", "c");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setDevice(UsbDevice device) {
        if ( device != null ) {
            UsbInterface intf = null;
            UsbEndpoint epOut = null;
            UsbEndpoint epIn = null;
            mLog.append("vendor id: "+device.getVendorId());
            mLog.append("\nproduct id: "+device.getProductId());
            for ( int i = 0; i < device.getInterfaceCount(); i++ ) {
                UsbInterface usbIf = device.getInterface( i );
                UsbEndpoint tOut = null;
                UsbEndpoint tIn = null;
                int tEndpointCnt = usbIf.getEndpointCount();
                for ( int j = 0; j < tEndpointCnt; j++ ) {
                    if ( usbIf.getEndpoint( j ).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK ) {
                        if ( usbIf.getEndpoint( j ).getDirection() == UsbConstants.USB_DIR_OUT ) {
                            tOut = usbIf.getEndpoint( j );
                        } else if ( usbIf.getEndpoint( j ).getDirection() == UsbConstants.USB_DIR_IN ) {
                            tIn = usbIf.getEndpoint( j );
                        }
                    }
                }
                if ( tOut != null || tIn != null ) {
                    intf = usbIf;
                    mDevice = device;
                    mEndpointOut = tOut;
                    mEndpointIn = tIn;
                }
            }

            UsbDeviceConnection connection = tra.mUsbManager.openDevice( device );
            if ( connection != null && intf != null && connection.claimInterface( intf, true ) ) {
                mConnection = connection;
                mLog.setText( "\nConnection Successful\nTouch Smart Card at anytime\n" );
                btnClear.setVisibility( View.VISIBLE );
//                tra.mConnection.controlTransfer(0x40, 0, 0, 0, null, 0, 0);// reset
//                // mConnection.controlTransfer(0x40, 0, 1, 0, null, 0, 0);//clear Rx
//                tra.mConnection.controlTransfer(0x40, 0, 2, 0, null, 0, 0);// clear Tx
//                tra.mConnection.controlTransfer(0x40, 0x02, 0x0000, 0, null, 0, 0);// flow
//                // control
//                // none
//                tra.mConnection.controlTransfer(0x40, 0x03, 0x4138, 0, null, 0, 0); // baud rate 9600
//                tra.mConnection.controlTransfer(0x40, 0x04, 0x0008, 0, null, 0, 0);// data bit
//                // 8, parity
//                // none,
//                // stop bit
//                // 1, tx off
                parseUsb();
            } else {
                mLog.append( "\nConnection Failed\n" );
                mConnection = null;
            }
        }
    }


    private class UsbAsyncTask extends AsyncTask<String, String, String> {
        protected MainActivity ma;

        public UsbAsyncTask() {
            super();
        }

        protected String doInBackground(String... strings) {
            boolean hasData = true;
            String allData = "";
            byte[] byteA = new byte[mEndpointIn.getMaxPacketSize()];
            try {
                UsbRequest request = new UsbRequest();
                if (!request.initialize(mConnection, mEndpointIn)) {
                    return "";
                }
                while (hasData) {
                    allData = "";
                    hasData = mConnection.bulkTransfer(mEndpointIn, byteA, mEndpointIn.getMaxPacketSize(), 1000) >= 0;
                    String result = Arrays.toString(byteA);
                    if (!hasData || result.isEmpty()) {
                        hasData = false;
                        if (startedScan) {
                            finishedScan = true;
                        }
                        return "";
                    } else {
                        startedScan = true;
                        byte[] bA = byteA;
                        for (int i = 0; i < bA.length; i++) {
                            allData += (char)bA[i];
                        }
                        lastDataReceived += allData;
                    }
                }
                request.close();

            } catch (Exception e) {
                finishedScan = false;
                startedScan = false;
                lastDataReceived = "";
                return " Error while reading ";
            }
            return allData;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            if (startedScan && finishedScan) {
                mLog.setText(lastDataReceived);
                lastDataReceived = "";
                finishedScan = false;
                startedScan = false;
            }
            parseUsb();
        }
    }
}
