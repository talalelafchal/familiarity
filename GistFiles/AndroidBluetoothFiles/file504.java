public class MainActivity extends AppCompatActivity{
    //BTの設定
    private BluetoothAdapter mBluetoothAdapter; //BTアダプタ
    private BluetoothDevice mBtDevice; //BTデバイス
    private BluetoothSocket mBtSocket; //BTソケット
    private OutputStream mOutput; //出力ストリーム


    @Override
    protected void onCreate(Bundle savedInstanceState) {
         mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

         //端末がBluetoothに対応しているか
        if(mBluetoothAdapter.getDefaultAdapter() == null){
            Toast.makeText(this,"Bluetooth対応機器ではありません",Toast.LENGTH_SHORT).show();
            finish();
        }
        //Bluetoothが有効であるか
        if(!mBluetoothAdapter.isEnabled()){
            Toast.makeText(this,"BluetoothをONにします",Toast.LENGTH_SHORT).show();
            mBluetoothAdapter.enable();
        }

        //BluetoothのMACアドレスの定義
        mBtDevice = mBluetoothAdapter.getRemoteDevice("接続したいBluetoothMACアドレスを入れる");


        //接続が確立するまで少し待つ
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //OutputStreamの設定
        try {
            //シリアル通信をしたいためUUIDは対応するIDを入れる
            mBtSocket = mBtDevice.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            mBtSocket.connect();
            mOutput = mBtSocket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"接続できませんでした #1",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        //文字列送信
        try {
            mOutput.write("TestSend".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}