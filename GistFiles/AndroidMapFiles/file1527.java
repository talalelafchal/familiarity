package com.internship1week.dwango.triage;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainWearActivity extends WearableActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private TextView mTextViewHeart;
    private TextView mTextViewMeasuring;
    private Button mApplyButton;
    private int mHealthScore;
    private ArrayList<Integer> mHeartRates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Keep the Wear screen always on (for testing only!)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextViewMeasuring = (TextView) findViewById(R.id.activity_main_wear_measuring_text);
                mApplyButton = (Button) findViewById(R.id.activity_main_wear_apply_button);
                mApplyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onNextButtonClicked();
                    }
                });
                mTextViewHeart = (TextView) stub.findViewById(R.id.activity_main_wear_heartrate_text);
                getHeartRate();
            }
        });
        Intent intent = getIntent();
        mHealthScore = intent.getIntExtra("score", 0);
    }

    private void onNextButtonClicked() {
        int heartPoint = (int) calculateAverage(mHeartRates);
        Log.d("average", ""+ heartPoint);
        Intent intent = new Intent();
        intent.putExtra("score", mHealthScore + heartPoint);
    }

    private double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        if(!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    private void getHeartRate() {
        SensorManager mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values[0] != 0) {
            int heartRate = (int) event.values[0];
            String msg = "" + heartRate;
            mTextViewHeart.setVisibility(View.VISIBLE);
            mTextViewHeart.setText(msg);
            mTextViewMeasuring.setVisibility(View.VISIBLE);
            Log.d(TAG, msg);
            mHeartRates.add(heartRate);
        } else if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int) event.values[0];
            mTextViewMeasuring.setVisibility(View.INVISIBLE);
            mTextViewHeart.setVisibility(View.INVISIBLE);
            Log.d(TAG, msg);
        } else {
            mTextViewMeasuring.setVisibility(View.INVISIBLE);
            mTextViewHeart.setVisibility(View.INVISIBLE);
            Log.d(TAG, "Unknown sensor type");
        }
        if (mHeartRates.size() > 10) {
            mApplyButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}