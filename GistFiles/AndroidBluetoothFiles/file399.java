package fr.free.triviaux.sylvain.nucleochain;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "00:19:5D:EF:02:A2";

    //https://www.intorobotics.com/how-to-develop-simple-bluetooth-android-application-to-control-a-robot-remote/

    //From http://stackoverflow.com/questions/13450406/how-to-receive-serial-data-using-android-bluetooth
    BluetoothDevice mmDevice;
    BluetoothSocket mmSocket;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    Canvas canvas;
    Bitmap bg;

    Boolean bRotate = Boolean.TRUE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Check if terminal offer Bluetooth capabiliies
        if (bluetoothAdapter == null)
            Toast.makeText(MainActivity.this, "Bluetooth is absent from terminal app will not run",
                    Toast.LENGTH_SHORT).show();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else{
            checkBT();
        }

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));
        bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bg);
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.rect);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==REQUEST_ENABLE_BT) {


            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Sylvain BT cancelled",  Toast.LENGTH_SHORT).show();
            }

            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Sylvain BT OK",  Toast.LENGTH_SHORT).show();


            }

            checkBT();
        }
    }

    private void checkBT()
    {

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        showBTDialog();
    }


    private void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        if(mmDevice == null)
            Toast.makeText(MainActivity.this, "mmDevice NULL",
                    Toast.LENGTH_SHORT).show();
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);

        mmSocket.connect();


        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();


        beginListenForData();
    }


    public void showBTDialog() {

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.popup_bt, (ViewGroup) findViewById(R.id.listView));

        popDialog.setTitle("Paired Bluetooth Devices");
        popDialog.setView(Viewlayout);

        // create the arrayAdapter that contains the BTDevices, and set it to a ListView
        final ListView myListView = (ListView) Viewlayout.findViewById(R.id.listView);
        ArrayAdapter<String> BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        myListView.setAdapter(BTArrayAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long itemID) {

                View itemView = view;
                int position = (int) arg0.getSelectedItemId();
                String text = myListView.getItemAtPosition(arg2).toString().trim();
                String[] deviceData = text.split("\n");
                Toast.makeText(getApplicationContext(), deviceData[0], Toast.LENGTH_LONG).show();



                for (BluetoothDevice device :   bluetoothAdapter.getBondedDevices()) {
                    String deviceName = device.getName();
                    Toast.makeText(getApplicationContext(), deviceData[0], Toast.LENGTH_SHORT).show();
                    if(deviceName.equals(deviceData[0])) {
                        mmDevice = device;
                        Toast.makeText(MainActivity.this, text+" FOUND",
                                Toast.LENGTH_SHORT).show();
                    }
                }


                if(mmDevice == null)
                    Toast.makeText(MainActivity.this, "mmDevice NULL1",
                            Toast.LENGTH_SHORT).show();
                try
                {
                    openBT();
                }
                catch (IOException ex) {
                    Log.d("NUCLEORADAR", ex.getMessage());
                    Toast.makeText(MainActivity.this, ex.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // get paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // put it's one to the adapter
        for(
                BluetoothDevice device
                :pairedDevices)
            BTArrayAdapter.add(device.getName()+"\n"+device.getAddress());


        // Button OK
        popDialog.setPositiveButton("close",
                new DialogInterface.OnClickListener()

                {
                    public void onClick (DialogInterface dialog,int which){
                        dialog.dismiss();
                    }

                }

        );

        // Create popup and show
        popDialog.create();
        popDialog.show();

    }



    void beginListenForData()
    {


        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {

                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run() {
                                             String[] separated = data.split("[E,]+");
                                             final int numberOfSensors = (separated.length-1)/2 +1;
                                            String s_value;
                                            if (separated[0].length() == 0) {

                                                separated[0].trim();
                                                s_value = separated[1];

                                                final Integer value = Integer.parseInt(s_value);

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Integer width = canvas.getWidth() / 2;
                                                        Integer height = canvas.getHeight() / 2;
                                                        stDrawRect(50+ 50, canvas.getHeight()-200, canvas.getHeight(), 20, "#ffffff", canvas);
                                                        stDrawRect(50+ 50, canvas.getHeight()-200, value, 20, "#efe085", canvas);
                                                    }
                                                });
                                                int iloop;
                                                for(iloop=2; iloop<separated.length; iloop=iloop+2){
                                                final int i = iloop;
                                                        separated[i].trim();
                                                        s_value = separated[i];

                                                    final Integer  valuenew = Integer.parseInt(s_value);

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Integer width = canvas.getWidth() / 2;
                                                                Integer height = canvas.getHeight() / 2;
                                                                //canvas.drawColor(Color.WHITE);
                                                                String color = "#ff748c";
                                                                if(i==2)
                                                                {
                                                                    color = "#ff748c";
                                                                }

                                                                if(i==4)
                                                                {
                                                                    color = "#04afbf";
                                                                }

                                                                if(i>4)
                                                                {
                                                                    color = "#6e3493";
                                                                }

                                                                stDrawRect(50+ 50*(i+1), canvas.getHeight()-200, canvas.getHeight(), 20, "#ffffff", canvas);
                                                                stDrawRect(50+ 50*(i+1), canvas.getHeight()-200, valuenew, 20, color, canvas);
                                                            }
                                                        });
                                                }
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                        else
                        {
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }


    public void stDrawOnCanvas(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#6b8728"));
         int width = canvas.getWidth()/2;
        int height = canvas.getHeight()/2;
        float radius = 240;

        RelativeLayout ll = (RelativeLayout) findViewById(R.id.rect);
     }

    public void stDrawHalfCircle(int x, int y, float radius, String  color,  Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(Color.parseColor(color));

        for (double i = 0; i <= 180; i = i + 0.1) {
            double irad = Math.toRadians(i);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
             paint.setXfermode(null);
            canvas.drawLine(x, y, (float) ( (radius * Math.cos(irad))), (float) ( (radius * Math.sin(irad))), paint);
        }
    }


    public void stDrawLineAngle(int x, int y, float radius, Integer angle,  String  color,  Canvas canvas) {
        //http://www.color-hex.com/
        //#6b8728 = Green
        //#00ff6d = vert clair
        //#ff211e = rouge
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.rect);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));

        Paint paint = new Paint();
        paint.setColor( Color.parseColor(color));

        angle = 180-angle;
        double dang = (double) angle;


        double irad = Math.toRadians( dang);
        canvas.drawRect((float)(x-20), (float)radius, (float)(x+20),(float) y, paint);
    }


    public void stDrawRect(int x, int y, float radius,  Integer width, String  color,   Canvas canvas) {
        //http://www.color-hex.com/
        //#6b8728 = Green
        //#00ff6d = vert clair
        //#ff211e = rouge
        RelativeLayout ll = (RelativeLayout) findViewById(R.id.rect);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));
        radius = (float) (radius * 0.7);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(color));
        canvas.drawRect((float)(x-width/2), (float)y-radius, (float)(x+width/2),(float) y, paint);
    }



    public void stDrawBeam(int x, int y, float radius, Integer angle,  String  color,  Canvas canvas) {

        stDrawLineAngle(canvas.getWidth() / 2, canvas.getHeight() / 2, (float)  (radius), (Integer) angle-1, "#336600", canvas);
        stDrawLineAngle(canvas.getWidth() / 2, canvas.getHeight() / 2, (float)  (radius), (Integer) angle-2, "#4D9900", canvas);
        stDrawLineAngle(canvas.getWidth() / 2, canvas.getHeight() / 2, (float)  (radius), (Integer) angle-3, "#66CC00", canvas);
        stDrawLineAngle(canvas.getWidth() / 2, canvas.getHeight() / 2, (float)  (radius), (Integer) angle-4, "#80FF00", canvas);
        stDrawLineAngle(canvas.getWidth() / 2, canvas.getHeight() / 2, (float)  (radius), (Integer) angle-5, "#80FF00", canvas);
    }

}//class
