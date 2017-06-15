package app.locker;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LockScreenActivity extends Activity implements
		LockscreenUtils.OnLockStatusChangedListener {

	public static String input = "";
	public static String EVENT = "NULL";
	public int[] random_array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	ArrayList<Button> btn_list = new ArrayList<Button>();
	Button ok_btn, setting_btn;
	ImageView iv;

	private LockscreenUtils mLockscreenUtils;

	@Override
	public void onAttachedToWindow() {
		Log.d("ORDER", "LockScreenActivity.onAttachedToWindow");
		Log.d("ORDER", EVENT);
		this.getWindow().setType(
				WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		this.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		super.onAttachedToWindow();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("ORDER", "LockScreenActivity.onCreate");

		if (EVENT.equals("CHANGE_PASSWORD_OLD")) {
			Toast.makeText(LockScreenActivity.this, "Введите старый пароль",
					Toast.LENGTH_SHORT).show();
		}
		if (EVENT.equals("CHANGE_PASSWORD_NEW")) {
			Toast.makeText(LockScreenActivity.this, "Введите новый пароль",
					Toast.LENGTH_SHORT).show();
		}

		LoadPreferences();
		setContentView(R.layout.main);
		init();
		// unlock screen in case of app get killed by system
		if (getIntent() != null && getIntent().hasExtra("kill")
				&& getIntent().getExtras().getInt("kill") == 1) {
			enableKeyguard();
			unlockHomeButton();
		} else {

			try {
				// disable keyguard
				disableKeyguard();
				// lock home button
				lockHomeButton();
				// start service for observing intents
				startService(new Intent(this, LockscreenService.class));

				// listen the events get fired during the call
				StateListener phoneStateListener = new StateListener();
				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				telephonyManager.listen(phoneStateListener,
						PhoneStateListener.LISTEN_CALL_STATE);

			} catch (Exception e) {
			}
		}
	}

	public void LoadPreferences() {
		MainActivity.preferences = getSharedPreferences(
				MainActivity.PREFERENCES, Context.MODE_PRIVATE);
		MainActivity.password = MainActivity.preferences.getString("password",
				"1111");
		MainActivity.background = MainActivity.preferences.getInt("background",
				0);
	}

	private class StateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				unlockHomeButton();
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			}
		}
	};

	// Don't finish Activity on Back press
	@Override
	public void onBackPressed() {
		return;
	}

	// Handle button clicks
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
				|| (keyCode == KeyEvent.KEYCODE_POWER)
				|| (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
				|| (keyCode == KeyEvent.KEYCODE_CAMERA)) {
			return true;
		}
		if ((keyCode == KeyEvent.KEYCODE_HOME)) {

			return true;
		}

		return false;

	}

	// handle the key press events here itself
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
				|| (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
				|| (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
			return false;
		}
		if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {

			return true;
		}
		return false;
	}

	// Lock home button
	public void lockHomeButton() {
		mLockscreenUtils.lock(LockScreenActivity.this);
	}

	// Unlock home button and wait for its callback
	public void unlockHomeButton() {
		mLockscreenUtils.unlock();
	}

	// Simply unlock device when home button is successfully unlocked
	@Override
	public void onLockStatusChanged(boolean isLocked) {
		if (!isLocked) {
			unlockDevice();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		unlockHomeButton();
	}

	@SuppressWarnings("deprecation")
	private void disableKeyguard() {
		KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
		mKL.disableKeyguard();
	}

	@SuppressWarnings("deprecation")
	private void enableKeyguard() {
		KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
		mKL.reenableKeyguard();
	}

	// Simply unlock device by finishing the activity
	private void unlockDevice() {
		finish();
	}

	public void init() {
		mLockscreenUtils = new LockscreenUtils();
		iv = (ImageView) findViewById(R.id.background);
		iv.setImageResource(MainActivity.imageIDs[MainActivity.background]);

		ok_btn = (Button) findViewById(R.id.buttonOK);

		btn_list.add((Button) findViewById(R.id.button1));
		btn_list.add((Button) findViewById(R.id.button2));
		btn_list.add((Button) findViewById(R.id.button3));
		btn_list.add((Button) findViewById(R.id.button4));
		btn_list.add((Button) findViewById(R.id.button5));
		btn_list.add((Button) findViewById(R.id.button6));
		btn_list.add((Button) findViewById(R.id.button7));
		btn_list.add((Button) findViewById(R.id.button8));
		btn_list.add((Button) findViewById(R.id.button9));
		btn_list.add((Button) findViewById(R.id.button10));

		shuffleArray(random_array);

		for (int i = 0; i < btn_list.size(); i++)
			btn_list.get(i).setText(Integer.toString(random_array[i]));

		ok_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (EVENT == "CHANGE_PASSWORD_OLD") {
					if (!input.equals(MainActivity.password))
						Toast.makeText(LockScreenActivity.this,
								"Неверный пароль", Toast.LENGTH_SHORT).show();
					else {
						EVENT = "CHANGE_PASSWORD_NEW";
						Toast.makeText(LockScreenActivity.this,
								"Введите новый пароль", Toast.LENGTH_SHORT)
								.show();
					}

				} else if (EVENT == "CHANGE_PASSWORD_NEW") {

					if (input.length() < 4 || input.length() > 10)
						Toast.makeText(LockScreenActivity.this,
								"Длина пароля должна быть от 4 до 10 символов",
								Toast.LENGTH_SHORT).show();
					else {
						MainActivity.password = input;
						EVENT = "NULL";
						unlockHomeButton();
					}

				} else if (EVENT == "NULL") {
					if (!input.equals(MainActivity.password))
						Toast.makeText(LockScreenActivity.this,
								"Неверный пароль", Toast.LENGTH_SHORT).show();
					else
						unlockHomeButton();
				}

				input = "";
			}

		});

		setting_btn = (Button) findViewById(R.id.settings);
		setting_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LockScreenActivity.this.startActivity(new Intent(
						LockScreenActivity.this, MainActivity.class));
			}

		});

	}

	public void onClick(View v) {
		input += ((Button) v).getText().toString();
		// Toast.makeText(KeyActivity.this, input, Toast.LENGTH_SHORT).show();
	}

	public void shuffleArray(int[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

}
