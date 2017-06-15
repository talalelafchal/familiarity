package com.tbg.robot_control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends FragmentActivity implements SensorEventListener{

    public static boolean bDebug = false;

    private StringBuilder sbGyro = new StringBuilder(),
                            sbAccel = new StringBuilder();

    boolean isDBthreadStopped;
    float fOffsetRX, fOffsetRY, fOffsetRZ;
    float fTimeX;
    long fTimeSend;

    boolean bIsCalibratedAccel = false,
            bIsCalibratedGyro = false;

    // threads
    static CalcThread[] thAccel = new CalcThread[3]; // x,y,z

//    private final Handler uiHandlerAccel;

    // hardware sensors
    private SensorManager mSM;
    private Sensor sensGyro;
    private Sensor sensAccel;

    // ui
    private TextView tvGyroValues, tvAccelValues;
    private TextView tvBTReceived;
    private TextView tvTemperatureDatetime, tvTemperatureValue;

    private Button btnCalibAccel;
    private Button btnCalibGyro;
    private Button btnGotoGraphs;
    private Button btnClearDB;

    private final Calendar calendar;
    private GraphFragment fragGraph;
    private final float[] fAvgAccelArray = new float[3];
    private final float[] fOffsetAccelArray = new float[3];
    private final TemperatureDB dbTemperature = new TemperatureDB(this);


    public MainActivity()
    {
        fTimeX = 0f;
        fTimeSend = System.nanoTime();
//        uiHandlerAccel = new Handler();
        calendar = Calendar.getInstance();
        thAccel[0] = new CalcThread("accel X");
        thAccel[1] = new CalcThread("accel Y");
        thAccel[2] = new CalcThread("accel Z");
    }



    void calibrateGyro(float val[])
    {
        fOffsetRX = val[0];
        fOffsetRY = val[1];
        fOffsetRZ = val[2];

        bIsCalibratedGyro = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // simple values
        String strMsg;


        // UI handles
        tvGyroValues = (TextView)findViewById(R.id.tv_main_gyro_values);
        tvAccelValues = (TextView)findViewById(R.id.tv_main_accel_values);
        tvBTReceived = (TextView)findViewById(R.id.tv_main_bt_feedback);
        tvTemperatureDatetime = (TextView)findViewById(R.id.tv_main_temperature_datetime);
        tvTemperatureValue = (TextView)findViewById(R.id.tv_main_temperature_value);

        btnCalibAccel = (Button)findViewById(R.id.btn_main_calib_accel);
        btnCalibGyro = (Button)findViewById(R.id.btn_main_calib_gyro);
        btnGotoGraphs = (Button)findViewById(R.id.btn_main_graphsactivity);
        btnClearDB = (Button)findViewById(R.id.btn_main_clr_db);

        if(findViewById(R.id.frame_main_container_fragments) != null)
        {
            fragGraph = GraphFragment.newInstance((byte)3);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_main_container_fragments, fragGraph).commit();


        }

        // hardware handles
        mSM = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        // ctrl+B
        getSensors();



        // callbacks
        btnCalibAccel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for(byte i=0;i<3;i++)
                    thAccel[i].setIsCalibrated(false);
            }
        });

        btnCalibGyro.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bIsCalibratedGyro = false;
            }
        });

        if(btnGotoGraphs != null)
        {
            btnGotoGraphs.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent iGraphs = new Intent(MainActivity.this, GraphsActivity.class);
                    startActivity(iGraphs);
                }
            });
        }

        btnClearDB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dbTemperature.deleteAllRecords();
            }
        });


        // database stuff
//       dbTemperature.onUpgrade(dbTemperature.getWritableDatabase(), 4,5);

        // only in Portrait
        if(tvTemperatureDatetime != null && tvTemperatureValue != null)
        {
            DBReadWriteThread();
        }
    }

    /**
     *  get handle to hardware sensors;
     *  start threads that calculate averages
     */
    void getSensors()
    {
        //      gyro
        sensGyro = mSM.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(sensGyro == null)
        {
            Toast.makeText(this,
                    "Your Device Lacks a Gyroscope (!!!)",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,
                    "Gyroscope handle obtained.",
                    Toast.LENGTH_SHORT).show();
        }


        //      acceleration
        sensAccel = mSM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensAccel == null)
        {
            Toast.makeText(this,
                    "Your Device Lacks an Accelerometer(!!!)",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,
                    "Accelerometer handle obtained.",
                    Toast.LENGTH_SHORT).show();

            // start thread calculating stuff
            for(byte i=0;i<3;i++)
                thAccel[i].start();
        }

    }

    void updateGyroUI(float[] val)
    {
        // calc offsets, if user hits button
        if(!bIsCalibratedGyro)
            calibrateGyro(val);

        // clear feedback string
        sbGyro = new StringBuilder();

        sbGyro.append("rx: " + (Math.round(val[0]-fOffsetRX)) + "\n");
        sbGyro.append("ry: " + (Math.round(val[1]-fOffsetRY)) + "\n");
        sbGyro.append("rz: " + (Math.round(val[2]-fOffsetRZ)) + "\n");

        if (tvGyroValues != null)
        {
            tvGyroValues.setText(sbGyro.toString());
        }

        if(fragGraph!=null)
            fragGraph.setGyrData(new DataPoint(++fTimeX,(val[0]-fOffsetRX)));

    }


    void updateAccelUI(final float[] val, final float[] offset)
    {
        sbAccel = new StringBuilder();


        int nax = Math.round(val[0] - offset[0]);
        sbAccel.append("ax: " + nax + "\n");
        sbAccel.append("ay: " + Math.round(val[1]-offset[1]) + "\n");
        sbAccel.append("az: " + Math.round(val[2]-offset[2]) + "\n");

        if(tvAccelValues != null)
            tvAccelValues.setText(sbAccel);

        DataPoint[] data = {
                new DataPoint(++fTimeX, (val[0] - offset[0])),
                new DataPoint(fTimeX, (val[1] - offset[1])),
                new DataPoint(fTimeX, (val[2] - offset[2]))
        };

        if(fragGraph!=null)
            fragGraph.setAccData((byte)3, data);


    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            // ctrl+B
            updateGyroUI(event.values);
        }
        else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {

            // test: calc X,Y,Z
            for(byte a=0;a<3;a++)
            {
                thAccel[a].giveFloat(event.values[a]);
                fAvgAccelArray[a] = thAccel[a].getAvg();
                fOffsetAccelArray[a] = thAccel[a].getOffset();
            }
            // ctrl+B
            updateAccelUI(fAvgAccelArray, fOffsetAccelArray);

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume()
    {
        super.onResume();

//        getSensors();
        mSM.registerListener(this, sensGyro, SensorManager.SENSOR_DELAY_GAME);
        mSM.registerListener(this, sensAccel, SensorManager.SENSOR_DELAY_GAME);

        isDBthreadStopped = false;
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // unregister all, save power
        mSM.unregisterListener(this);
        isDBthreadStopped = true;
    }

    public void onDestroy()
    {
        // stop calc threads
        for(byte a=0;a<3;a++)
            thAccel[a].close();


        // stop & close bluetooth
        try
        {
            BTClose();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // just to be safe
        isBTthreadStopped = true;
        isDBthreadStopped = true;


        super.onDestroy();
    }

    // Bluetooth
    private final String BT_DEVICE_NAME = "HC-06";
    private final String BT_DEVICE_ADDRESS="28:be:03:7c:3d:49"; // of the external btDevice to be paired
    final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID (bt boards)

    private BluetoothDevice btDevice;
    private BluetoothSocket btSocket;
    private OutputStream outputStream;
    private InputStream inputStream;

    boolean isBTdeviceConnected =false;

    byte buffer[];
//    int bufferPosition;
    boolean isBTthreadStopped;


    public boolean BTinit()
    {
        boolean bDeviceFound=false;
        BluetoothAdapter btAdapter=BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null)
            Toast.makeText(this,
                    "Your Device doesn't Support Bluetooth.",
                    Toast.LENGTH_SHORT).show();

        if(!btAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
        }


        Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(this,"Please Pair the Device first",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (BluetoothDevice itr : bondedDevices)
            {
//                if(itr.getAddress().equals(DEVICE_ADDRESS))
                if(itr.getName().equals(BT_DEVICE_NAME))
                {
                    btDevice = itr;
                    bDeviceFound=true;
                    Toast.makeText(this,
                                        "BT Device acquired.",
                                        Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }


        Toast.makeText(this,
                            "BTinit() complete.",
                            Toast.LENGTH_SHORT).show();

        return bDeviceFound;
    }

    public boolean BTconnect()
    {
        boolean isConnected;

        try {
            btSocket = btDevice.createRfcommSocketToServiceRecord(PORT_UUID);
            btSocket.connect();
            if(btSocket == null)
                Log.d("BTConnect", "rfcomm socket didn't happen");

            isConnected = true;
        }
        catch (IOException e) {
            e.printStackTrace();
            isConnected=false;
        }

        if(isConnected)
        {
            try
            {
                outputStream= btSocket.getOutputStream();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.d("BTConnect", "outputstream wasn't assigned.");
            }


            try
            {
                inputStream= btSocket.getInputStream();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.d("BTConnect", "inputstream wasn't assigned.");
            }

        }

        Toast.makeText(this,
                "BTconnect() complete.",
                Toast.LENGTH_SHORT).show();

        return isConnected;
    }

    public void BTClose() throws IOException
    {
        isBTthreadStopped = true;
        if(outputStream !=null)
            outputStream.close();
        if(inputStream!=null)
            inputStream.close();
        if(btSocket != null)
            btSocket.close();
        isBTdeviceConnected =false;
        Toast.makeText(this, "BT Connection Closed.", Toast.LENGTH_SHORT).show();
    }

    public void onClickStart(View view)
    {
        if(BTinit()) // this statement calls that function. BTinit() will return.
        {
            if(BTconnect()) // same. BTconnect() will return.
            {
                // success, so flip this flag
                isBTdeviceConnected =true;

                // connected, so now...do stuff
//                BTListenDataThread();
                BTSendDataThread();

                // feedback
                Toast.makeText(this,
                                    "BT Connection Opened.",
                                    Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,
                                    "BT Connection not opened.",
                                     Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this,
                    "BT not initialized.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    void BTListenDataThread()
    {
        final Handler handler = new Handler();
        isBTthreadStopped = false;
//        buffer = new byte[1024];

        // put on a separate thread
        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !isBTthreadStopped)
                {
                    try
                    {
                        int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string=new String(rawBytes,"UTF-8");



                            handler.post(new Runnable() {
                                public void run()
                                {
                                    tvBTReceived.setText(string);
                                    Log.d("BTListenDataThread", string);
                                }
                            });

                        }
                    }
                    catch (IOException e)
                    {
                        isBTthreadStopped = true;
                    }
                }
            }
        });

        thread.start();
    }

    void BTSendDataThread()
    {
        Thread thSend = new Thread("thread: sending bt data")
        {
            @Override
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !isBTthreadStopped)
                {
                    long now = System.nanoTime();
                    long diff = now - fTimeSend;
                    int nax = (int)(fAvgAccelArray[0] - fOffsetAccelArray[0]);

                    // limit transmission frequency: lock to timestep
                    if(isBTdeviceConnected && (diff > 150f*1000f*1000f))
                    {
                        String strNAX = String.valueOf(nax);
                        BTsendData(strNAX);
                        // reset time reference
                        fTimeSend = System.nanoTime();
                        Log.d("diff", ""+diff);
                    }

                }
            }
        };

        thSend.start();
    }

    public void onClickSend(View view)
    {
        String string = "0";

        BTsendData(string);
    }

    public void onClickStop(View view) throws IOException
    {
        BTClose();
    }

    void BTsendData(String args)
    {
        try
        {
            if(outputStream!=null)
            {
                outputStream.write(args.getBytes());

                for(byte val: args.getBytes())
                    Log.d("BTsendData", ""+val);
            }
            else
            {
                Log.d("BTsendData","outputStream is null");

            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    void DBReadWriteThread()
    {
        final Handler handler = new Handler();
        final Random rand = new Random();
        new Thread(new Runnable()
        {
            long now;
            long diff;
            Cursor cur;

            @Override
            public void run()
            {
                while(!isDBthreadStopped)
                {
                    now = System.nanoTime();
                    diff = now - fTimeSend;
                    if(diff > 1000f*1000f*1000f) // nanosec
                    {
                        dbTemperature.insertTemperatureValue(40 + (rand.nextDouble() * 100));
                        fTimeSend = System.nanoTime();

                        cur = dbTemperature.getAllTemperatureValues();
                        handler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                while (cur.moveToNext())
                                {
                                    tvTemperatureDatetime.setText("Datetme\n");
                                    tvTemperatureDatetime.append(cur.getString(cur.getColumnIndex(CONSTANTS.table_temperature_date)).toString());
                                    tvTemperatureValue.setText("Temperature (*F)\n");
                                    tvTemperatureValue.append("" + cur.getDouble(cur.getColumnIndex(CONSTANTS.table_temperature_value)));
                                }
                            }
                        });
                    }
                }
            }
        }).start();

    }
}