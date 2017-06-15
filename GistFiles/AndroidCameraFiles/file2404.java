package gclue.com.camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;

class MyView extends View implements SensorEventListener {

    private int x;
    private int y;
    private SensorManager mSensorManager;
    private Float gyroX;
    private Float gyroY;
    private Float gyroZ;

    /**
     * コンストラクタ.
     *
     * @param context
     */
    public MyView(Context context) {
        super(context);
        setFocusable(true);

        mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

        // Sensorの取得とリスナーへの登録.
        List<Sensor> gyroSensors = mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
        if (gyroSensors.size() > 0) {
            Sensor gyroSensor = gyroSensors.get(0);
            mSensorManager.registerListener(this, gyroSensor, SENSOR_DELAY_FASTEST);
        }
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
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}