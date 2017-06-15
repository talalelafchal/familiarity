package com.sira_lab.bluetoothcontroller;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener, OnTouchListener, OnSeekBarChangeListener {
	
	public static final int PROGRESSBAR_MAX = 2000;
	public static final int REQUEST_CONNECTDEVICE = 1;
	public static final int REQUEST_ENABLEBLUETOOTH = 2;
	private static final int MENUID_SEARCH = 0;
	
	private SensorManager manager;
	private TextView values;
	private SeekBar Steering;
	private BluetoothAdapter bluetoothadapter;
	private BluetoothServer bluetoothserver;
	private Button accelButton;
	
	private String labelText = "";
	private float pointY = 0;
	
	private final Handler handler = new Handler() {
		// ハンドルメッセージ
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothServer.MESSAGE_STATECHANGE:
				switch (msg.arg1) {
				case BluetoothServer.STATE_CONNECTED:
					// addText("接続完了");
					break;
				case BluetoothServer.STATE_CONNECTING:
					// addText("接続中");
					break;
				case BluetoothServer.STATE_NONE:
					// addText("未接続");
					break;
				}
				break;
			case BluetoothServer.MESSAGE_READ:
				// byte[] readBuf = (byte[]) msg.obj;
				// addText(new String(readBuf, 0, msg.arg1));
				break;
			}
		}
	};

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Steering = (SeekBar)findViewById(R.id.seekBar1);
        Steering.setMax(PROGRESSBAR_MAX);
        Steering.setProgress(PROGRESSBAR_MAX / 2);
        
        values = (TextView)findViewById(R.id.textView1);
        accelButton = (Button)findViewById(R.id.button1);
        accelButton.setOnTouchListener(this);
        
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        
        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothadapter == null) {
			// 端末がBluetoothをサポートしていない
		}

    }
    
	@Override
	public void onStart() {
		super.onStart();
		if (bluetoothadapter.isEnabled() == false) {
			// Bluetoothが無効になっているので、有効にする要求を発生さえる。
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLEBLUETOOTH);
		}
		else {
			if (null == bluetoothserver) {
				// BluetoothServer作成
				bluetoothserver = new BluetoothServer(this, handler);
			}
		}
	}
    
    @Override
	public void onStop() {
    	super.onStop();
    	manager.unregisterListener(this);
    }
    
    @Override
	public void onResume() {
    	super.onResume();
    	List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
    	if(sensors.size() > 0) {
    		Sensor s = sensors.get(0);
    		manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
    	}
    }

    @Override
	public void onDestroy() {
		super.onDestroy();
		if (null != bluetoothserver) {
			bluetoothserver.stop();
		}
    }
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECTDEVICE: // 端末検索
			if (Activity.RESULT_OK == resultCode) {
				String address = data.getExtras().getString(DeviceListActivity.EXTRANAME_DEVICEADDRESS);

				// Bluetooth接続要求
				BluetoothDevice device = bluetoothadapter.getRemoteDevice(address);
				bluetoothserver.connect(device);
			}
			break;
		case REQUEST_ENABLEBLUETOOTH: // Bluetooth有効化
			if (Activity.RESULT_OK == resultCode) {
				bluetoothserver = new BluetoothServer(this, handler);
			}
			else {
				Toast.makeText(this, "Bluetoothが有効ではありません", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}
    
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		/* if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			labelText = "X軸:" + event.values[0]
					+ "\nY軸:" + event.values[1] 
					+ "\nZ軸:" + event.values[2];
			values.setText(labelText);
		} */
		
		pointY = (float) (pointY * 0.85 + event.values[1] * 0.15);
		
		// Steering.setProgress((int) ((pointY * 0.9 + event.values[1] * 0.1) * 100 + 1000));
		Steering.setProgress((int) (pointY * 100 + 1000));
		// Steering.setProgress((int) (event.values[1] * 100 + 1000));
		
		Log.d("onProgressChanged", String.valueOf(pointY));
		String strMessage = "";
		strMessage = "$SIRARC,," + String.valueOf(event.values[1]*10);
		int iLength = strMessage.length();
		int iChecksum = 0;
		for (int i = 1; i < iLength; ++i) {
			iChecksum ^= strMessage.charAt(i);
		}
		strMessage += "*" + String.format("%02X", iChecksum);
		strMessage += "\r\n";
		
		if (0 != strMessage.length()) {
			if (null != bluetoothserver) {
				try {
					bluetoothserver.write(strMessage.getBytes());
				}
				catch (Exception e) {
					Log.d("onProgressChanged", "通信失敗しました");
					// addText("通信失敗しました");
				}
			}
			else {
				Log.d("onProgressChanged", "bluetoothserver == null");
			}
		}
	}

	
	// オプションメニュー生成時
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem item0 = menu.add(0, MENUID_SEARCH, 0, "端末検索");
		item0.setIcon(android.R.drawable.ic_search_category_default);
		
		return true;
	}

	// オプションメニュー選択時
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case MENUID_SEARCH:
			Intent devicelistactivityIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(devicelistactivityIntent, REQUEST_CONNECTDEVICE);
			
			return true;
		}
		
		return false;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
		Log.d("onProgressChanged", String.valueOf(progress));
		String strMessage = "";
		
		if (seekBar == Steering) {
			strMessage = "$SIRARC,," + String.valueOf(progress - 100);
			int iLength = strMessage.length();
			int iChecksum = 0;
			for (int i = 1; i < iLength; ++i) {
				iChecksum ^= strMessage.charAt(i);
			}
			strMessage += "*" + String.format("%02X", iChecksum);
			strMessage += "\r\n";
		}
		
		if (0 != strMessage.length()) {
			if (null != bluetoothserver) {
				try {
					bluetoothserver.write(strMessage.getBytes());
				}
				catch (Exception e) {
					Log.d("onProgressChanged", "通信失敗しました");
					// addText("通信失敗しました");
				}
			}
			else {
				Log.d("onProgressChanged", "bluetoothserver == null");
			}
		}
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if (v == accelButton) {
			
			String strMessage = "";
			
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				
				Log.d("onProgressChanged", String.valueOf(1));
				
				strMessage = "$SIRARC," + String.valueOf(1) + ",";
				int iLength = strMessage.length();
				int iChecksum = 0;
				for (int i = 1; i < iLength; ++i) {
					iChecksum ^= strMessage.charAt(i);
				}
				strMessage += "*" + String.format("%02X", iChecksum);
				strMessage += "\r\n";
				
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				
				Log.d("onProgressChanged", String.valueOf(0));
				
				strMessage = "$SIRARC," + String.valueOf(0) + ",";
				int iLength = strMessage.length();
				int iChecksum = 0;
				for (int i = 1; i < iLength; ++i) {
					iChecksum ^= strMessage.charAt(i);
				}
				strMessage += "*" + String.format("%02X", iChecksum);
				strMessage += "\r\n";
			}
			
			if (0 != strMessage.length()) {
				if (null != bluetoothserver) {
					try {
						bluetoothserver.write(strMessage.getBytes());
					}
					catch (Exception e) {
						Log.d("onProgressChanged", "通信失敗しました");
						;// addText("通信失敗しました");
					}
				}
				else {
					Log.d("onProgressChanged", "bluetoothserver == null");
				}
			}
		}
		
		return false;
	}
}
