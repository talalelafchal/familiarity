/*
 * SBBLE(Konashi) sample program
 * Copyright (C) 2013 Yuuichi Akagawa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ammlab.android.bletest;

import android.app.Activity;
import android.bluetooth.*;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.view.Menu;

import java.util.UUID;


public class MainActivity extends Activity implements BluetoothAdapter.LeScanCallback {
    /** BLE 機器スキャンタイムアウト (ミリ秒) */
    private static final long SCAN_PERIOD = 10000;
    /** 検索機器の機器名 */
    private static final String DEVICE_NAME = "SBBLE";
    /** 対象のサービスUUID */
//    private static final String DEVICE_BUTTON_SENSOR_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
  private static final String DEVICE_KONASHI_SERVICE_UUID = "0000ff00-0000-1000-8000-00805f9b34fb";
    /** 対象のキャラクタリスティックUUID */
//    private static final String DEVICE_BUTTON_SENSOR_CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_KONASHI_PIO_SETTING_UUID            = "00003000-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_KONASHI_PIO_PULLUP_UUID             = "00003001-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_KONASHI_PIO_OUTPUT_UUID             = "00003002-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_KONASHI_PIO_INPUT_NOTIFICATION_UUID = "00003003-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_KONASHI_PWM_CONFIG_UUID             = "00003004-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_KONASHI_PWM_PARAM_UUID              = "00003005-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_KONASHI_PWM_DUTY_UUID               = "00003006-0000-1000-8000-00805f9b34fb";
    
    /** キャラクタリスティック設定UUID */
    private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    private static final byte KONASHI_FAILURE = (byte)(0xff);

    //PIO定義
    private static final byte PIO0 = 0;
    private static final byte PIO1 = 1;
    private static final byte PIO2 = 2;
    private static final byte PIO3 = 3;
    private static final byte PIO4 = 4;
    private static final byte PIO5 = 5;
    private static final byte PIO6 = 6;
    private static final byte PIO7 = 7;
    private static final byte MODE_INPUT = 0;
    private static final byte MODE_OUTPUT = 1;
    private static final byte NO_PULLS = 0;
    private static final byte PULLUP   = 1;
    private static final byte LOW  = 0;
    private static final byte HIGH = 1;
    //PWM定義
    private static final byte KONASHI_PWM_DISABLE = 0;
    private static final byte KONASHI_PWM_ENABLE = 1;
    private static final byte KONASHI_PWM_ENABLE_LED_MODE = 2;
    private static final int  KONASHI_PWM_LED_PERIOD = 10000;
    // Konashi UART baudrate
    private static final byte KONASHI_UART_RATE_2K4 = 0x0a;
    private static final byte KONASHI_UART_RATE_9K6 = 0x28;
    // Konashi UART
    private static final byte KONASHI_UART_DATA_MAX_LENGTH = 19;
    private static final byte KONASHI_UART_DISABLE = 0;
    private static final byte KONASHI_UART_ENABLE = 1;
    
    //Konashi internal variables
    private byte pinModeSetting = 0;
    private byte pioPullup = 0;
    private byte pioInput = 0;
    private byte pioOutput = 0;
    private byte pwmSetting = 0;
    private int [] pwmDuty;
    private int [] pwmPeriod;
    
    private static final String TAG = "SBBLEtest";
    private BleStatus mStatus = BleStatus.DISCONNECTED;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mBluetoothGatt;
    private TextView mStatusText;
    private ToggleButton mToggleButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//konashi
		pwmDuty   = new int[8];
		pwmPeriod = new int[8];
		for(int i=0; i<8; i++){
			pwmDuty[i] = 0;
			pwmPeriod[i] = 0;
		}
		
	    mToggleButton = (ToggleButton) findViewById(R.id.toggleButton1);

	    mBluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
 
        mToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                byte value = (byte) (isChecked ? 0x0 : 0x1);
                //sendCommand(value);
                digitalWrite(PIO0, value);
                /*
            	byte[] val = new byte[1];
            	val[0] = value;

                BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(DEVICE_KONASHI_SERVICE_UUID));
                BluetoothGattCharacteristic characteristic1 =
                        service.getCharacteristic(UUID.fromString(CHARACTERISTIC_KONASHI_PIO_OUTPUT_UUID));
                if (characteristic1 != null) {
                    // キャラクタリスティックが見つかった
                	byte[] val = new byte[1];
                	val[0] = value;
                	characteristic1.setValue(val);
                	mBluetoothGatt.writeCharacteristic(characteristic1);
                }
                */

              
            }
        });

        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
        findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
 
        mStatusText = (TextView)findViewById(R.id.text_status);
 
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mStatusText.setText(((BleStatus) msg.obj).name());
            }
        };
	}
	
    @Override
    public void onPause() {
        super.onPause();
        //disconnect();
    }

    @Override
    public void onDestroy() {
    	disconnect();
        super.onDestroy();
    }
    
    /** BLE機器を検索する */
    private void connect() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(MainActivity.this);
                if (BleStatus.SCANNING.equals(mStatus)) {
                    setStatus(BleStatus.SCAN_FAILED);
                }
            }
        }, SCAN_PERIOD);
 
        mBluetoothAdapter.stopLeScan(this);
        mBluetoothAdapter.startLeScan(this);
        setStatus(BleStatus.SCANNING);
    }
 
    /** BLE 機器との接続を解除する */
    private void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            setStatus(BleStatus.CLOSED);
        }
    }
    
    /** Pinモード設定 */
    /* 0:Input, 1:Output   */
    private void pinMode(byte pin, byte mode) {
    	if(pin >= PIO0 && pin <= PIO7 && (mode == MODE_OUTPUT || mode == MODE_INPUT)){
    		if(mode == MODE_OUTPUT){
    			pinModeSetting |= (byte)(0x01 << pin);
    		}else{
    			pinModeSetting &= (byte)(~(0x01 << pin) & 0xFF);
    		}
    		byte[] val = new byte[1];
    		val[0] = pinModeSetting;
            Log.d(TAG, "pinMode: " + pin + "," + mode + "," + pinModeSetting);

    		writeValue(DEVICE_KONASHI_SERVICE_UUID, CHARACTERISTIC_KONASHI_PIO_SETTING_UUID, val);
    	}
    }
    
    /** Pin PullUp設定 */
    /* 0:NO_PULLS, 1:PULLUP   */
    private void pinPullUp(byte pin, byte mode) {
        if(pin >= PIO0 && pin <= PIO7 && (mode == PULLUP || mode == NO_PULLS)){
            // Set value
            if(mode == PULLUP){
                pioPullup |= (byte)(0x01 << pin);
            } else {
                pioPullup &= (byte)(~(0x01 << pin) & 0xFF);
            }
       		byte[] val = new byte[1];
    		val[0] = pioPullup;
            Log.d(TAG, "pinPullUp: " + pin + "," + mode + "," + pioPullup);

    		writeValue(DEVICE_KONASHI_SERVICE_UUID, CHARACTERISTIC_KONASHI_PIO_PULLUP_UUID, val);
        }
    }
    /** Pin PullUp設定  (byte) */
    private void pinPullUpAll(byte value){
        // Set value
        pioOutput = value;
        
        // Write value
   		byte[] val = new byte[1];
		val[0] = pioOutput;
		writeValue(DEVICE_KONASHI_SERVICE_UUID, CHARACTERISTIC_KONASHI_PIO_PULLUP_UUID, val);
    }
    
    /** PIO 読み込み */
    private byte digitalRead(byte pin){
    	if(pin >= PIO0 && pin <= PIO7){
    		return (byte)((pioInput >> pin) & 0x01);
    	}else{
    		return KONASHI_FAILURE;
    	}
    }

    /** PIO 書き込み (bit) */
    private void digitalWrite(byte pin, byte value){
        if(pin >= PIO0 && pin <= PIO7 && (value == HIGH || value == LOW)){
            // Set value
            if(value == HIGH){
                pioOutput |= 0x01 << pin;
            }
            else{
                pioOutput &= ~(0x01 << pin) & 0xFF;
            }
            // Write value
       		byte[] val = new byte[1];
    		val[0] = pioOutput;
    		writeValue(DEVICE_KONASHI_SERVICE_UUID, CHARACTERISTIC_KONASHI_PIO_OUTPUT_UUID, val);
        }
    }
    /** PIO 書き込み (byte) */
    private void digitalWriteAll(byte value){
        // Set value
        pioOutput = value;
        
        // Write value
   		byte[] val = new byte[1];
		val[0] = pioOutput;
		writeValue(DEVICE_KONASHI_SERVICE_UUID, CHARACTERISTIC_KONASHI_PIO_OUTPUT_UUID, val);
    }
    
    /** PWMモード設定 */
    private void pwmMode(byte pin, byte mode) {
    	if(pin >= PIO0 && pin <= PIO7 &&  (mode == KONASHI_PWM_DISABLE || mode == KONASHI_PWM_ENABLE || mode == KONASHI_PWM_ENABLE_LED_MODE )){
            // Set value
            if(mode == KONASHI_PWM_ENABLE || mode == KONASHI_PWM_ENABLE_LED_MODE){
                pwmSetting |= 0x01 << pin;
            }
            else{
                pwmSetting &= ~(0x01 << pin) & 0xFF;
            }
            if (mode == KONASHI_PWM_ENABLE_LED_MODE){
            	//TODO
            }
    		byte[] val = new byte[1];
    		val[0] = pinModeSetting;
            Log.d(TAG, "pinMode: " + pin + "," + mode + "," + pinModeSetting);

    		writeValue(DEVICE_KONASHI_SERVICE_UUID, CHARACTERISTIC_KONASHI_PWM_CONFIG_UUID, val);
    	}
    }
    /** PWM設定 */
    private void pwmPeriod(byte pin, int period) {
        if(pin >= PIO0 && pin <= PIO7 && pwmDuty[pin] <= period){
            pwmPeriod[pin] = period;
    		byte[] val = new byte[5];
    		val[0] = pin;
    		val[1] = (byte)((pwmPeriod[pin] >> 24) & 0xFF);
    		val[2] = (byte)((pwmPeriod[pin] >> 16) & 0xFF);
    		val[3] = (byte)((pwmPeriod[pin] >> 8) & 0xFF);
    		val[4] = (byte)((pwmPeriod[pin] >> 0) & 0xFF);
    		writeValue(DEVICE_KONASHI_SERVICE_UUID, CHARACTERISTIC_KONASHI_PWM_PARAM_UUID, val);
        }
    }
    /** PWM設定 */
    private void pwmDuty(byte pin, int duty) {
        if(pin >= PIO0 && pin <= PIO7 && duty <= pwmPeriod[pin]){
        	pwmDuty[pin] = duty;
    		byte[] val = new byte[5];
    		val[0] = pin;
    		val[1] = (byte)((pwmDuty[pin] >> 24) & 0xFF);
    		val[2] = (byte)((pwmDuty[pin] >> 16) & 0xFF);
    		val[3] = (byte)((pwmDuty[pin] >> 8) & 0xFF);
    		val[4] = (byte)((pwmDuty[pin] >> 0) & 0xFF);
    		writeValue(DEVICE_KONASHI_SERVICE_UUID, CHARACTERISTIC_KONASHI_PWM_DUTY_UUID, val);
        }
    }
    /** PWM設定 */
    private void pwmLedDrive(byte pin, float ratio) {
        if(ratio < 0.0){
            ratio = 0.0f;
        }
        if(ratio > 100.0){
            ratio = 100.0f;
        }
        int duty = (int)(KONASHI_PWM_LED_PERIOD * ratio / 100);
        pwmDuty(pin, duty);
    }

    /** BLE機器への書き込み */
    private void writeValue(String uuidService, String uuidCharacteristic, byte[] value) {
        if (mBluetoothGatt != null) {
        	BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(uuidService));
        	BluetoothGattCharacteristic characteristic =
                    service.getCharacteristic(UUID.fromString(uuidCharacteristic));
            if (characteristic != null) {
                // キャラクタリスティックが見つかった
            	characteristic.setValue(value);
            	mBluetoothGatt.writeCharacteristic(characteristic);
            }

        }
    }
 

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(TAG, "device found: " + device.getName() + " " + device.getAddress());
//      Log.d(TAG, "device found: " + device.getName());
        if (DEVICE_NAME.equals(device.getName())) {
            setStatus(BleStatus.DEVICE_FOUND);
 
            // 省電力のためスキャンを停止する
            mBluetoothAdapter.stopLeScan(this);
 
            // GATT接続を試みる
            mBluetoothGatt = device.connectGatt(this, false, mBluetoothGattCallback);
        }
    }
 
    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange: " + status + " -> " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // GATTへ接続成功
                // サービスを検索する
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // GATT通信から切断された
                setStatus(BleStatus.DISCONNECTED);
                mBluetoothGatt = null;
            }
        }
 
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered received: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(UUID.fromString(DEVICE_KONASHI_SERVICE_UUID));
                if (service == null) {
                    // サービスが見つからなかった
                    setStatus(BleStatus.SERVICE_NOT_FOUND);
                } else {
                    // サービスを見つけた
                    setStatus(BleStatus.SERVICE_FOUND);
 
                	pinMode(PIO0, MODE_OUTPUT);
                	pinPullUp(PIO1, PULLUP);
                	digitalWrite(PIO0, HIGH);
/*
                    BluetoothGattCharacteristic characteristic1 =
                            service.getCharacteristic(UUID.fromString(CHARACTERISTIC_KONASHI_PIO_SETTING_UUID));
                    if (characteristic1 != null) {
                        // キャラクタリスティックが見つかった
                    	byte[] val = new byte[1];
                    	val[0] = 0x01;
                    	characteristic1.setValue(val);
                    	gatt.writeCharacteristic(characteristic1);
                    }
                    characteristic1 =
                            service.getCharacteristic(UUID.fromString(CHARACTERISTIC_KONASHI_PIO_PULLUP_UUID));
                    if (characteristic1 != null) {
                        // キャラクタリスティックが見つかった
                    	byte[] val = new byte[1];
                    	val[0] = 0x0f;
                    	characteristic1.setValue(val);
                    	gatt.writeCharacteristic(characteristic1);
                    }

                	BluetoothGattCharacteristic characteristic1 =
                            service.getCharacteristic(UUID.fromString(CHARACTERISTIC_KONASHI_PIO_OUTPUT_UUID));
                    if (characteristic1 != null) {
                        // キャラクタリスティックが見つかった
                    	byte[] val = new byte[1];
                    	val[0] = 0x01;
                    	characteristic1.setValue(val);
                    	gatt.writeCharacteristic(characteristic1);
                    }

*/
                    BluetoothGattCharacteristic characteristic =
                            service.getCharacteristic(UUID.fromString(CHARACTERISTIC_KONASHI_PIO_INPUT_NOTIFICATION_UUID));
 
                    if (characteristic == null) {
                        // キャラクタリスティックが見つからなかった
                        setStatus(BleStatus.CHARACTERISTIC_NOT_FOUND);
                    } else {
                        // キャラクタリスティックを見つけた
 
                        // Notification を要求する
                        boolean registered = gatt.setCharacteristicNotification(characteristic, true);
 
                        // Characteristic の Notification 有効化
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
 
                        if (registered) {
                            // Characteristics通知設定完了
                            setStatus(BleStatus.NOTIFICATION_REGISTERED);
                        } else {
                            setStatus(BleStatus.NOTIFICATION_REGISTER_FAILED);
                        }
                    }
                }
            }
        }
 
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.d(TAG, "onCharacteristicRead: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // READ成功
            }
        }
 
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged");
            // Characteristicの値更新通知
 
            if (CHARACTERISTIC_KONASHI_PIO_INPUT_NOTIFICATION_UUID.equals(characteristic.getUuid().toString())) {
                Byte value = characteristic.getValue()[0];
                pioInput = value;
                boolean left = (0 < (value & 0x02));
                updateButtonState(left);
            }
        }
    };
 
    private void updateButtonState(final boolean left) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View leftView = findViewById(R.id.left);
                leftView.setBackgroundColor( (left ? Color.BLUE : Color.TRANSPARENT) );
            }
        });
    }
 
    private void setStatus(BleStatus status) {
        mStatus = status;
        mHandler.sendMessage(status.message());
    }
 
    private enum BleStatus {
        DISCONNECTED,
        SCANNING,
        SCAN_FAILED,
        DEVICE_FOUND,
        SERVICE_NOT_FOUND,
        SERVICE_FOUND,
        CHARACTERISTIC_NOT_FOUND,
        NOTIFICATION_REGISTERED,
        NOTIFICATION_REGISTER_FAILED,
        CLOSED
        ;
        public Message message() {
            Message message = new Message();
            message.obj = this;
            return message;
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
