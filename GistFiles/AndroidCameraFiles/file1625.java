package be.howest.nmct.tiltsensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class SensorActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView txbAcceleration;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        txbAcceleration = (TextView) findViewById(R.id.txbAcceleration);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        //X=[0] Y=[1] Z=[2]
        Integer x = Math.round(event.values[0]);
        Integer y = Math.round(event.values[1]);
        txbAcceleration.setText("X: "+Math.round(event.values[0])+"\nY: "+Math.round(event.values[1]));

        //CHECK X-AXIS
        if(x>=2){
            //Drive forward
            Log.i("DRIVEINFO","Driving forward. X="+x);
        }
        else if(x<=-2){
            //Drive backwards
            Log.i("DRIVEINFO","Driving backwards. X="+x);
        }
        else{
            //Stand still
        }

        //CHECK Y-AXIS
        if(y>=2){
            //Drive left
            Log.i("DRIVEINFO","Turning left. Y="+y);
        }
        else if(y<=-2){
            //Drive right
            Log.i("DRIVEINFO","Turning right. Y="+y);
        }
        else{
            //Stand still
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}