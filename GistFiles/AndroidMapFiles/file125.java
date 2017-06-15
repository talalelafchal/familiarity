package com.demo.led;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	private NotificationManager notificationManager;
	private Notification notification;

	private RadioGroup radioGroup;
	private RadioButton colorBtn_2;
	private RadioButton colorBtn_3;
	private RadioButton colorBtn_4;
	private Button startBT;
	private Button stopBT;
	private EditText input = null;

	private Map<String, Integer> colorMap = new HashMap<String, Integer>(5);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		colorMap.put("0xffff0000", -65536);
		colorMap.put("0xffffff00", -256);
		colorMap.put("0xff00ff00", -16711936);

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notification = new Notification();
		notification.flags = Notification.FLAG_SHOW_LIGHTS;

		colorBtn_2 = (RadioButton) findViewById(R.id.color_button_2);
		colorBtn_3 = (RadioButton) findViewById(R.id.color_button_3);
		colorBtn_4 = (RadioButton) findViewById(R.id.color_button_4);
		stopBT = (Button) findViewById(R.id.stopBT);
		startBT = (Button) findViewById(R.id.startBT);
		radioGroup = (RadioGroup) findViewById(R.id.color_button_group);
		radioGroup.setOnCheckedChangeListener(this);
		input = (EditText) findViewById(R.id.input);

		colorBtn_2.setTextColor(Color.RED);

		colorBtn_3.setTextColor(Color.YELLOW);

		colorBtn_4.setTextColor(Color.GREEN);

		stopBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopLed();
			}
		});

		startBT.setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (colorBtn_2.getId() == checkedId) {
			input.setText("#ff0000");
		} else if (colorBtn_3.getId() == checkedId) {
			input.setText("#ffff00");
		} else if (colorBtn_4.getId() == checkedId) {
			input.setText("#00ff00");
		}
	}

	@Override
	public void onClick(View v) {
		if (v == startBT) {
			stopLed();
			int ledColor = 0;
			try {
				ledColor = Color.parseColor(input.getText().toString());
			} catch (NumberFormatException e) {
				return;
			}
			startBT.setTextColor(ledColor);
			notification.ledARGB = ledColor;
			notification.ledOffMS = 200;
			notification.ledOnMS = 500;
			notificationManager.notify(0, notification);
		}
	}

	private void stopLed() {
		notificationManager.cancel(0);
	}

	@Override
	protected void onDestroy() {
		stopLed();
		super.onDestroy();
	}
}