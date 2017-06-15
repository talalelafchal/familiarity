import java.io.InputStream;
import java.util.Set;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;

public class BluetoothClient extends Activity implements Runnable{
  
	/**
	 * TAG
	 */
	private static final String TAG = "BT";
	
	/**
	 * Bluetooth Adapter
	 */
	private BluetoothAdapter mAdapter;
	
	/**
	 * Bluetooth Devices
	 */
	private BluetoothDevice mDevice;
	
	/**
	 * Bluetooth UUID
	 */
	private final UUID MY_UUID = UUID.fromString( "00001101-0000-1000-8000-00805F9B34FB");
	
	/**
	 * Device Name
	 */
	private final String DEVICE_NAME = "FireFly-BE68";
	
	/**
	 * Socket
	 */
	private BluetoothSocket mSocket;
	
	/**
	 * Thread
	 */
	private Thread mThread;
	
	/**
	 * Threadの状態を表す
	 */
	private boolean isRunning;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Bluetoothのデバイス名を取得
		// デバイス名は、FireFly-BEXXになるため、XXの数字はデバイス毎に異なる。
		// DVICE_NAMEでデバイス名を定義
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		Set< BluetoothDevice > devices = mAdapter.getBondedDevices();	
		for ( BluetoothDevice device : devices ){
			Log.i(TAG,"DEVICE:"+device.getName());
			if(device.getName().equals(DEVICE_NAME)){
				mDevice = device;
			}
		}
		
		// Threadを起動し、Bluetooth接続
		mThread = new Thread(this);
		isRunning = true;
		mThread.start();
	}

	@Override
	protected void onPause(){
		super.onPause();
		
		isRunning = false;
		try{
			mSocket.close();
		}
		catch(Exception e){}
	}

	@Override
	public void run() {
		InputStream mmInStream = null;
		
		try {
			// 取得したデバイス名を使ってBluetoothでSocket接続
			mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
			mSocket.connect();
			mmInStream = mSocket.getInputStream();
		
			// InputStreamのバッファを格納
			byte[] buffer = new byte[1024];
			// 取得したバッファのサイズを格納
			int bytes;
		
			while(isRunning){
				// InputStreamの読み込み　
				bytes = mmInStream.read(buffer);
				
				// String型に変換
				String readMsg = new String(buffer, 0, bytes);
				
				// null以外なら表示
				if(readMsg.trim() != null && !readMsg.trim().equals("")){
					Log.i(TAG,"value="+readMsg.trim());
				}
				else{
					Log.i(TAG,"value=nodata");
				}
			}
		}catch(Exception e){
			Log.e(TAG,"error:"+e);
			try{
				mSocket.close();
			}catch(Exception ee){}
			isRunning = false;
		}
	}
}