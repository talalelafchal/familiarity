package ivy.kookkai;

import ivy.kookkai.data.GlobalVar;
import ivy.kookkai.debugview.CameraInterface;
import ivy.kookkai.debugview.DebugImgView;
import ivy.kookkai.debugview.FieldView;
import ivy.kookkai.debugview.HomographyPointsView;
import ivy.kookkai.debugview.LocalizationView;
import ivy.kookkai.debugview.UndistortView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.renderscript.Mesh.Primitive;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KookKaiDroidActivity extends Activity implements
  	SensorEventListener {
	/** Called when the activity is first created. */

	private final int FIELDVIEWHEIGHT = 200;
	CameraInterface cameraInterface;
	MainlLoop main;
	DebugImgView debugImgview;
	FieldView fieldView;
	UndistortView undistortView;
	HomographyPointsView homographyView;
	LocalizationView localizationView;
	TextView debugText;
	SensorManager sensorManager, compassManager, gyroManager;
	Context mContext;
	float[] acc = new float[3];
	float[] orient = new float[3];
	float[] mag_MAX = { 35, 52, 48 };
	float[] mag_MIN = { -54, 8, -9 };

	// float[] mag_MAX = {0,0,0};
	// float[] mag_MIN = {1000,1000,1000};

	// float[] gyro = new float[] { 0, 0, 0 };
	// private static final float NS2S = 1.0f / 1000000000.0f;
	// long timestamp=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		LinearLayout rootHorizontalLayout = (LinearLayout) findViewById(R.id.upper_view);

		Log.d("ivy_debug", "start!!!");

		mContext = this.getApplicationContext();

		// LinearLayout bgLayout =
		// (LinearLayout)findViewById(R.id.backgroud_layout);
		// bgLayout.setOnKeyListener(myKeyListener);

		// TODO move all the code into res/layout/mainxml
		// Left Vertical Layout zone
		cameraInterface = new CameraInterface(this);
		cameraInterface.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		debugImgview = new DebugImgView(this);
		debugImgview.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		FrameLayout cameraFrame = new FrameLayout(this);
		cameraFrame
				.setLayoutParams(new LayoutParams(
						cameraInterface.frameHeight / 2,
						cameraInterface.frameWidth / 2));
		cameraFrame.addView(cameraInterface);
		cameraFrame.addView(debugImgview);

		Button exitButton = new Button(this);
		exitButton.setFocusable(false);
		exitButton.setText("exit!!");
		exitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		// CheckBox cb = (CheckBox) findViewById(R.id.checkBox1);
		CheckBox cb = new CheckBox(this);
		cb.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		cb.setChecked(false);
		cb.setText("draw color");
		cb.setFocusable(false);

		Log.d("frame", "button height:");// why can't get button height
		LinearLayout leftVerticalLayout = new LinearLayout(this);
		leftVerticalLayout.setLayoutParams(new LayoutParams(
				cameraInterface.frameHeight / 2,
				cameraInterface.frameWidth / 2 + 150));// FIELDVIEWHEIGHT +
														// UndistortView.VIEWHEIGHT
														// + 400));
		leftVerticalLayout.setOrientation(LinearLayout.VERTICAL);
		leftVerticalLayout.addView(cameraFrame);
		leftVerticalLayout.addView(exitButton);
		leftVerticalLayout.addView(cb);

		// Right Vertical Layout zone
		FrameLayout fieldFrame = new FrameLayout(this);

		fieldView = new FieldView(this);
		fieldView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				FIELDVIEWHEIGHT));

		localizationView = new LocalizationView(this);
		localizationView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, FIELDVIEWHEIGHT));

		fieldFrame.addView(fieldView);
		fieldFrame.addView(localizationView);

		undistortView = new UndistortView(this);
		undistortView.setLayoutParams(new LayoutParams(undistortView.VIEWWIDTH,
				undistortView.VIEWHEIGHT));

		homographyView = new HomographyPointsView(this);
		homographyView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// FrameLayout undistortFrame = new FrameLayout(this);
		// LinearLayout.LayoutParams llp = new
		// LinearLayout.LayoutParams(UndistortView.VIEWWIDTH,UndistortView.VIEWHEIGHT);
		// llp.gravity = Gravity.CENTER;
		// undistortFrame.setLayoutParams(llp);
		// undistortFrame.addView(undistortView);

		LinearLayout undistort_n_homo = new LinearLayout(this);
		undistort_n_homo.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		undistort_n_homo.setOrientation(LinearLayout.HORIZONTAL);
		undistort_n_homo.addView(undistortView);
		undistort_n_homo.addView(homographyView);

		LinearLayout rightVerticalLayout = new LinearLayout(this);
		rightVerticalLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, FIELDVIEWHEIGHT
						+ UndistortView.VIEWHEIGHT));
		rightVerticalLayout.setOrientation(LinearLayout.VERTICAL);
		rightVerticalLayout.addView(fieldFrame);
		// rightVerticalLayout.addView(undistortFrame);
		rightVerticalLayout.addView(undistort_n_homo);

		// TODO continue
		// Lower Zone

		debugText = (TextView) findViewById(R.id.debugText);
		// Dont know why can'y use below
		// debugText = new TextView(this);
		// debugText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT));
		// debugText.setText("Text");
		// leftVerticalLayout.addView(debugText);

		rootHorizontalLayout.addView(leftVerticalLayout);
		rootHorizontalLayout.addView(rightVerticalLayout);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		compassManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		compassManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_NORMAL);
		// gyroManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// gyroManager.registerListener(this,
		// sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
		// SensorManager.SENSOR_DELAY_NORMAL);
		main = new MainlLoop(cameraInterface, debugImgview, localizationView,
				undistortView, homographyView, debugText, cb);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		return keyHandle(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		super.onKeyUp(keyCode, event);
		return keyHandle(keyCode, event);
	}

	public boolean keyHandle(int keyCode, KeyEvent event) {
		boolean state;
		if (event.getAction() == KeyEvent.ACTION_DOWN)
			state = true;
		else
			state = false;

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			GlobalVar.joyData.dUp = state;
			break;

		case KeyEvent.KEYCODE_DPAD_DOWN:
			GlobalVar.joyData.dDown = state;
			break;

		case KeyEvent.KEYCODE_DPAD_LEFT:
			GlobalVar.joyData.dLeft = state;
			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT:
			GlobalVar.joyData.dRight = state;
			break;

		case KeyEvent.KEYCODE_1:
			GlobalVar.joyData.one = state;
			break;
		case KeyEvent.KEYCODE_2:
			GlobalVar.joyData.two = state;
			break;

		case KeyEvent.KEYCODE_A:
			GlobalVar.joyData.a = state;
			break;
		case KeyEvent.KEYCODE_B:
			GlobalVar.joyData.b = state;
			break;

		default:
			break;
		}

		// Log.d("keypress", Integer.toString(keyCode));
		// Toast.makeText(mContext, "press"+state , Toast.LENGTH_SHORT).show();

		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
		main.start();
		// debugImgview.fill(color.white);
		// debugImgview.invalidate();

	}

	@Override
	public void onStop() {
		super.onStop();
		// main.stop();
	}

	public void onDestroy() {
		super.onDestroy();
		main.stop();

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			GlobalVar.ax = acc[0] = event.values[0];
			GlobalVar.ay = acc[1] = event.values[1];
			GlobalVar.az = acc[2] = event.values[2];

		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			float R[] = new float[9];
			float mValues[] = new float[3];

			SensorManager.getRotationMatrix(R, null, acc, event.values);
			SensorManager.getOrientation(R, mValues);
			orient[0] = event.values[0];
			orient[1] = event.values[1];
			orient[2] = event.values[2];
			Log.d("act_mag", "" + (int) orient[0] + "," + (int) orient[1] + ","
					+ (int) orient[2]);

			// if(orient[0]>mag_MAX[0])mag_MAX[0]=orient[0];
			// if(orient[1]>mag_MAX[1])mag_MAX[1]=orient[1];
			// if(orient[2]>mag_MAX[2])mag_MAX[2]=orient[2];
			// if(orient[0]<mag_MIN[0])mag_MIN[0]=orient[0];
			// if(orient[1]<mag_MIN[1])mag_MIN[1]=orient[1];
			// if(orient[2]<mag_MIN[2])mag_MIN[2]=orient[2];
			// Log.d("mag_MAX ",""+(int)mag_MAX[0]+","+(int)mag_MAX[1]+","+(int)mag_MAX[2]+" ,  "
			// +(int)mag_MIN[0]+","+(int)mag_MIN[1]+","+(int)mag_MIN[2]);
			double[] norm_direction = new double[3];
			double[] direction = new double[3];
			double[] range = new double[3];
			for (int i = 0; i < range.length; i++) {
				range[i] = mag_MAX[i] - mag_MIN[i];
				norm_direction[i] = (orient[i] - mag_MIN[i] - range[i] / 2)
						/ range[i];
			}
			direction[0] = Math.atan2(norm_direction[0], norm_direction[2]);
			direction[1] = Math.atan2(norm_direction[0], norm_direction[1]);
			direction[2] = Math.atan2(norm_direction[1], norm_direction[2]);

			Log.d("mag_arctan", "" + (int) (direction[0] * 100) + ","
					+ (int) (direction[1] * 100) + ","
					+ (int) (direction[2] * 100));

			double tan = Math.atan2(orient[0] / mag_MAX[0], orient[2]
					/ mag_MAX[2]);
			// double angle;
			// if(orient[0]==0 && orient[2]>0)
			// angle = 180;
			// else if(orient[0]==0)
			// angle = 0;
			// else if(orient[2]==0 && orient[0]>0)
			// angle =90;
			// else if(orient[2]==0)
			// angle =270;
			// else
			// angle = orient[0]/orient[2];
			// tan =Math.at
			// Log.d("arc_tan", ""+ tan*90/Math.PI);

		}
		// else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
		// if(timestamp!=0){
		// float dT = (event.timestamp - timestamp)*NS2S;
		// gyro[0] += event.values[0]*dT;
		// gyro[1] += event.values[1]*dT;
		// gyro[2] += event.values[2]*dT;
		// Log.d("gyro", "" + (int) (gyro[0]*100) + "," + (int) (gyro[1]*100) +
		// ","
		// + (int) (gyro[2]*100));
		// Log.d("timestamp","time:"+timestamp);
		// }
		// timestamp=event.timestamp;
		// }
	}
}
