
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// import R;
public class MainActivity extends Activity {
	PendingIntent mPermissionIntent;
	Button btnCheck;
	TextView textInfo;
	UsbDevice device;
	UsbManager manager;
//    LanPrinter test;
	private static final String ACTION_USB_PERMISSION = "com.mobilemerit.usbhost.USB_PERMISSION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//        test = new LanPrinter(this);
		btnCheck = (Button) findViewById(R.id.check);
		textInfo = (TextView) findViewById(R.id.info);
		btnCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				textInfo.setText("");
				checkInfo();
			}
		});

	}

	private void checkInfo() {
		manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		/*
		 * this block required if you need to communicate to USB devices it's
		 * take permission to device
		 * if you want than you can set this to which device you want to communicate   
		 */
		// ------------------------------------------------------------------
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		registerReceiver(mUsbReceiver, filter);
		// -------------------------------------------------------------------
		HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		String i = "";
		while (deviceIterator.hasNext()) {
			device = deviceIterator.next();
			manager.requestPermission(device, mPermissionIntent);
			i += "\n" + "DeviceID: " + device.getDeviceId() + "\n"
					+ "DeviceName: " + device.getDeviceName() + "\n"
					+ "DeviceClass: " + device.getDeviceClass() + " - "
					+ "DeviceSubClass: " + device.getDeviceSubclass() + "\n"
					+ "VendorID: " + device.getVendorId() + "\n"
					+ "ProductID: " + device.getProductId() + "\n"
                    + device.toString() + "\n";
		}

		textInfo.setText(i);
	}



	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {
							// call method to set up device communication
						}
					} else {
                        Toast.makeText(MainActivity.this, " permission denied for device " + device, Toast.LENGTH_SHORT).show();
//                        Log.d("ERROR", "permission denied for device " + device);
					}
				}
			}
		}
	};

//    private void testPrint(){
//        test.execute();
//    }
}