package gclue.com.camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;

class MyView extends View implements SensorEventListener, LocationListener {

    private int x;
    private int y;
    private SensorManager mSensorManager;
    private Float gyroX;
    private Float gyroY;
    private Float gyroZ;
    private Float accelX;
    private Float accelY;
    private Float accelZ;
    private LocationManager mLocationManager;
    private Double lat;
    private Double lon;
    private Float rotateX;
    private Float rotateY;
    private Float rotateZ;
    private Float distance;
    private Float direction;

    /**
     * コンストラクタ.
     *
     * @param context
     */
    public MyView(Context context) {
        super(context);
        setFocusable(true);

        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

        // Rotationセンサーの取得とリスナーへの登録.
        List<Sensor> rotationSensors = mSensorManager.getSensorList(Sensor.TYPE_ROTATION_VECTOR);
        if (rotationSensors.size() > 0) {
            Sensor rotationSensor = rotationSensors.get(0);
            mSensorManager.registerListener(this, rotationSensor, SENSOR_DELAY_FASTEST);
        }

        // Sensorの取得とリスナーへの登録.
        List<Sensor> accelSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (accelSensors.size() > 0) {
            Sensor accelSensor = accelSensors.get(0);
            mSensorManager.registerListener(this, accelSensor, SENSOR_DELAY_FASTEST);
        }

        // Sensorの取得とリスナーへの登録.
        List<Sensor> gyroSensors = mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
        if (gyroSensors.size() > 0) {
            Sensor gyroSensor = gyroSensors.get(0);
            mSensorManager.registerListener(this, gyroSensor, SENSOR_DELAY_FASTEST);
        }

        // GPSの取得.
        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    /**
     * 描画処理.
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 描画するための線の色を設定.
        Paint mPaint = new Paint();
        mPaint.setStyle( Paint.Style.FILL );
        mPaint.setARGB( 255, 255, 255, 100 );
        mPaint.setTextSize(50);

        // 長方形を描画.
        canvas.drawRect( x, y, x + 100, y + 100, mPaint );

        // 文字を描画.
        canvas.drawText(""+gyroX, 150, 50, mPaint);
        canvas.drawText(""+gyroY, 150, 110, mPaint);
        canvas.drawText(""+gyroZ, 150, 170, mPaint);

        // 文字を描画.
        canvas.drawText(""+accelX, 550, 50, mPaint);
        canvas.drawText(""+accelY, 550, 110, mPaint);
        canvas.drawText(""+accelZ, 550, 170, mPaint);

        // 文字を描画.
        canvas.drawText(""+lat, 950, 50, mPaint);
        canvas.drawText(""+lon, 950, 110, mPaint);

        // 文字を描画.
        canvas.drawText(""+rotateX, 1350, 50, mPaint);
        canvas.drawText(""+rotateY, 1350, 110, mPaint);
        canvas.drawText(""+rotateZ, 1350, 170, mPaint);

        // 文字を描画.
        canvas.drawText(""+distance, 150, 250, mPaint);
        canvas.drawText(""+direction, 150, 310, mPaint);
        if(direction != null) {
            canvas.drawText("" + direction / 360.0, 150, 370, mPaint);
        }

    }

    /**
     * タッチイベント.
     */
    public boolean onTouchEvent(MotionEvent event) {

        // X,Y座標の取得.
        x = (int)event.getX();
        y = (int)event.getY();

        // 再描画の指示.
        invalidate();

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroX = event.values[0];
            gyroY = event.values[1];
            gyroZ = event.values[2];

            // 再描画の指示.
            invalidate();
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX = event.values[0];
            accelY = event.values[1];
            accelZ = event.values[2];

            // 再描画の指示.
            invalidate();
        } else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            rotateX = event.values[0];
            rotateY = event.values[1];
            rotateZ = event.values[2];

            // 再描画の指示.
            invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();

        Location fujisanLocation = new Location("fujisan");
        fujisanLocation.setLatitude(35.360744);
        fujisanLocation.setLongitude(138.727810);

        distance = location.distanceTo(fujisanLocation);
        direction = location.bearingTo(fujisanLocation);

        // 再描画の指示.
        invalidate();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}