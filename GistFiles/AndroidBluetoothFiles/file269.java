/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package edu.cwru.sail.myocollector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.XYPlot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.cwru.sail.portablegaitlab.LiveData;
import edu.cwru.sail.portablegaitlab.PGLActivity;
import edu.cwru.sail.portablegaitlab.R;
import edu.cwru.sail.portablegaitlab.SpinnerPlotChoiceListener;

// This sample illustrates how to attach to multiple Myo devices and distinguish between them.
@EActivity
public class MultipleMyosActivity extends Activity {
    private LiveData mLeftLiveData;
    private LiveData mRightLiveData;
    private ViewPager mViewPager;
    public static int count;
    //private PagerTitleStrip mPagerTitleStrip;
    private static final String TAG = "MultipleMyosActivity";
    // We store each Myo object that we attach to in this list, so that we can keep track of the order we've seen
    // each Myo and give it a unique short identifier (see onAttach() and identifyMyo() below).
    private ArrayList<Myo> mKnownMyos = new ArrayList<Myo>();
    private MyoAdapter mAdapter;
    private Button button;
    private Button button2;
    private TextView text;
    FileWriter Writer1 = null;
    FileWriter Writer2 = null;
    File data1;
    File data2;
    protected int ready = 0;
    Object[] buffer = new Object[11];
    int bufferReady = 0;
    @ViewById(R.id.which_plot_myo) protected Spinner mPlotChoice;
    @ViewById(R.id.left_plot_myo) protected XYPlot mPlotLeft;
    @ViewById(R.id.right_plot_myo) protected XYPlot mPlotRight;

    private DeviceListener mListener = new AbstractDeviceListener() {

        // Every time the SDK successfully attaches to a Myo armband, this function will be called.
        //
        // You can rely on the following rules:
        //  - onAttach() will only be called once for each Myo device
        //  - no other events will occur involving a given Myo device before onAttach() is called with it
        //
        // If you need to do some kind of per-Myo preparation before handling events, you can safely do it in onAttach().
        @Override
        public void onAttach(Myo myo, long timestamp) {

            // The object for a Myo is unique - in other words, it's safe to compare two Myo references to
            // see if they're referring to the same Myo.

            // Add the Myo object to our list of known Myo devices. This list is used to implement identifyMyo() below so
            // that we can give each Myo a nice short identifier.
            mKnownMyos.add(myo);

            // Now that we've added it to our list, get our short ID for it and print it out.
            Log.i(TAG, "Attached to " + myo.getMacAddress() + ", now known as Myo " + identifyMyo(myo) + ".");
        }

        @Override
        public void onConnect(Myo myo, long timestamp) {
            mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " has connected.");
        }

        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " is unsync.");
            if (ready == 1) {
                ready = 0;
                Toast.makeText(MultipleMyosActivity.this, "Myo" + identifyMyo(myo) + "unsync, data recording stop", Toast.LENGTH_SHORT).show();
                text.setText("Push button to record data!");
            }
            try {
                Writer1.flush();
                Writer2.flush();
                Writer1.close();
                Writer2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " has disconnected.");
            if (ready == 1) {
                ready = 0;
                Toast.makeText(MultipleMyosActivity.this, "Myo" + identifyMyo(myo) + "disconnected, data recording stop", Toast.LENGTH_SHORT).show();
                text.setText("Push button to record data!");
            }
            try {
                Writer1.flush();
                Writer2.flush();
                Writer1.close();
                Writer2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " switched to pose " + pose.toString() + ".");
        }

        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            if (ready == 1) {
                final double x = rotation.x();
                final double y = rotation.y();
                final double z = rotation.z();
                final double w = rotation.w();
                if(count == 0) {
                    addEntry(x, y, z, w, (identifyMyo(myo).length()));
                }

                buffer[1] = new Double (x);
                buffer[2] = new Double (y);
                buffer[3] = new Double (z);
                buffer[4] = new Double (w);
                bufferReady++;
                if (bufferReady == 3) {
                    writeCSV(timestamp,buffer, MyoConnectWriter(myo));
                    bufferReady = 0;
                }
            }
        }

        @Override
        public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
            if (ready == 1) {
                double x = accel.x();
                double y = accel.y();
                double z = accel.z();
                buffer[5] = new Double(x);
                buffer[6] = new Double(y);
                buffer[7] = new Double(z);
                bufferReady++;
                if(count == 1) {
                    addEntry(x, y, z, 1, (identifyMyo(myo).length()) + 10);
                }
                if(bufferReady == 3) {
                    writeCSV(timestamp,buffer, MyoConnectWriter(myo));
                    bufferReady = 0;
                }
            }
        }

        @Override
        public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
            if (ready == 1) {
                double x = gyro.x();
                double y = gyro.y();
                double z = gyro.z();
                if(count == 2) {
                    addEntry(x, y, z, 1, (identifyMyo(myo).length()) + 20);
                }
                buffer[8] = new Double(x);
                buffer[9] = new Double(y);
                buffer[10] = new Double(z);
                bufferReady++;
                if(bufferReady == 3) {
                    writeCSV(timestamp, buffer, MyoConnectWriter(myo));
                    bufferReady = 0;
                }
            }
        }
    };

    public void addEntry(double x, double y,double z ,double w, int myo){
        switch (myo){
            case 4:
                mLeftLiveData.updateOri(x,y,z,w);
                break;
            case 5:
                mRightLiveData.updateOri(x,y,z,w);
                break;
            case 14:
                mLeftLiveData.updateAcc(x, y, z);
                break;
            case 15:
                mRightLiveData.updateAcc(x,y,z);
                break;
            case 24:
                mLeftLiveData.updateGyro(x, y, z);
                break;
            case 25:
                mRightLiveData.updateGyro(x,y,z);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_myos);
        text = (TextView) findViewById(R.id.text);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);
        hubInit();
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          mPlotLeft.setVisibility(View.VISIBLE);
                                          mPlotRight.setVisibility(View.VISIBLE);
                                          //initialize all file here in order to reduce delay
                                          //orientation left and right
                                          long time = System.currentTimeMillis();
                                          if (identifyMyo(mKnownMyos.get(0)) == "LEFT") {
                                              data1 = new File(Environment.getExternalStorageDirectory(), "/myodata/data_LEFT" + time + ".csv");
                                              try {
                                                  Writer1 = new FileWriter(data1);
                                                  Writer1.append("Timestamp");
                                                  Writer1.append(",");
                                                  Writer1.append("ORIx");
                                                  Writer1.append(",");
                                                  Writer1.append("ORIy");
                                                  Writer1.append(",");
                                                  Writer1.append("ORIz");
                                                  Writer1.append(",");
                                                  Writer1.append("ORIw");
                                                  Writer1.append(",");
                                                  Writer1.append("ACCx");
                                                  Writer1.append(",");
                                                  Writer1.append("ACCy");
                                                  Writer1.append(",");
                                                  Writer1.append("ACCz");
                                                  Writer1.append(",");
                                                  Writer1.append("GYROx");
                                                  Writer1.append(",");
                                                  Writer1.append("GYROy");
                                                  Writer1.append(",");
                                                  Writer1.append("GYROz");
                                                  Writer1.append('\n');
                                                  Writer1.flush();//??????
                                              } catch (IOException e) {
                                                  Toast.makeText(MultipleMyosActivity.this, "IOexception", Toast.LENGTH_LONG).show();
                                              }
                                              //After initialzing, trigger the int to begin record data
                                              ready = 1;
                                              text.setText("Data is recording....");
                                          }

                                          if (identifyMyo(mKnownMyos.get(0)) == "RIGHT") {
                                              data2 = new File(Environment.getExternalStorageDirectory(), "/myodata/data_RIGHT" + time + ".csv");
                                              try {
                                                  Writer2 = new FileWriter(data2);
                                                  Writer2.append("Timestamp");
                                                  Writer2.append(",");
                                                  Writer2.append("ORIx");
                                                  Writer2.append(",");
                                                  Writer2.append("ORIy");
                                                  Writer2.append(",");
                                                  Writer2.append("ORIz");
                                                  Writer2.append(",");
                                                  Writer2.append("ORIw");
                                                  Writer2.append(",");
                                                  Writer2.append("ACCx");
                                                  Writer2.append(",");
                                                  Writer2.append("ACCy");
                                                  Writer2.append(",");
                                                  Writer2.append("ACCz");
                                                  Writer2.append(",");
                                                  Writer2.append("GYROx");
                                                  Writer2.append(",");
                                                  Writer2.append("GYROy");
                                                  Writer2.append(",");
                                                  Writer2.append("GYROz");
                                                  Writer2.append('\n');
                                                  Writer2.flush();//??????
                                              } catch (IOException e) {
                                                  Toast.makeText(MultipleMyosActivity.this, "IOexception", Toast.LENGTH_LONG).show();
                                              }
                                              //After initialzing, trigger the int to begin record data
                                              ready = 1;
                                              text.setText("Data is recording....");
                                          }

                                          if(mKnownMyos.size() == 2) {
                                             if (identifyMyo(mKnownMyos.get(0)) == "RIGHT" || identifyMyo(mKnownMyos.get(1)) == "RIGHT") {
                                                 data2 = new File(Environment.getExternalStorageDirectory(), "/myodata/data_RIGHT" + time + ".csv");
                                                 try {
                                                     Writer2 = new FileWriter(data2);
                                                     Writer2.append("Timestamp");
                                                     Writer2.append(",");
                                                     Writer2.append("ORIx");
                                                     Writer2.append(",");
                                                     Writer2.append("ORIy");
                                                     Writer2.append(",");
                                                     Writer2.append("ORIz");
                                                     Writer2.append(",");
                                                     Writer2.append("ORIw");
                                                     Writer2.append(",");
                                                     Writer2.append("ACCx");
                                                     Writer2.append(",");
                                                     Writer2.append("ACCy");
                                                     Writer2.append(",");
                                                     Writer2.append("ACCz");
                                                     Writer2.append(",");
                                                     Writer2.append("GYROx");
                                                     Writer2.append(",");
                                                     Writer2.append("GYROy");
                                                     Writer2.append(",");
                                                     Writer2.append("GYROz");
                                                     Writer2.append('\n');
                                                     Writer2.flush();//??????
                                                 } catch (IOException e) {
                                                     Toast.makeText(MultipleMyosActivity.this, "IOexception", Toast.LENGTH_LONG).show();
                                                 }
                                                 //After initialzing, trigger the int to begin record data
                                                 ready = 1;
                                                 text.setText("Data is recording....");
                                             }

                                              if (identifyMyo(mKnownMyos.get(0)) == "LEFT" || identifyMyo(mKnownMyos.get(1)) == "LEFT") {
                                                  data1 = new File(Environment.getExternalStorageDirectory(), "/myodata/data_LEFT" + time + ".csv");
                                                  try {
                                                      Writer1 = new FileWriter(data1);
                                                      Writer1.append("Timestamp");
                                                      Writer1.append(",");
                                                      Writer1.append("ORIx");
                                                      Writer1.append(",");
                                                      Writer1.append("ORIy");
                                                      Writer1.append(",");
                                                      Writer1.append("ORIz");
                                                      Writer1.append(",");
                                                      Writer1.append("ORIw");
                                                      Writer1.append(",");
                                                      Writer1.append("ACCx");
                                                      Writer1.append(",");
                                                      Writer1.append("ACCy");
                                                      Writer1.append(",");
                                                      Writer1.append("ACCz");
                                                      Writer1.append(",");
                                                      Writer1.append("GYROx");
                                                      Writer1.append(",");
                                                      Writer1.append("GYROy");
                                                      Writer1.append(",");
                                                      Writer1.append("GYROz");
                                                      Writer1.append('\n');
                                                      Writer1.flush();//??????
                                                  } catch (IOException e) {
                                                      Toast.makeText(MultipleMyosActivity.this, "IOexception", Toast.LENGTH_LONG).show();
                                                  }
                                                  //After initialzing, trigger the int to begin record data
                                                  ready = 1;
                                                  text.setText("Data is recording....");
                                              }
                                          }
                                      }
                                  });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ready = 0;
                text.setText("Push button to record data!");
                Toast.makeText(MultipleMyosActivity.this, "File stored successfully under myodata folder", Toast.LENGTH_SHORT).show();
                try {
                    Writer1.flush();
                    Writer2.flush();
                    Writer1.close();
                    Writer2.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    private void CSVInit(File data, FileWriter Writer){
//        data = new File(Environment.getExternalStorageDirectory(), "/myodata/data1_" + ".csv");
//        try {
//            Writer = new FileWriter(data);
//            Writer.append("Timestamp");
//            Writer.append(",");
//            Writer.append("ORIx");
//            Writer.append(",");
//            Writer.append("ORIy");
//            Writer.append(",");
//            Writer.append("ORIz");
//            Writer.append(",");
//            Writer.append("ORIw");
//            Writer.append(",");
//            Writer.append("ACCx");
//            Writer.append(",");
//            Writer.append("ACCy");
//            Writer.append(",");
//            Writer.append("ACCz");
//            Writer.append(",");
//            Writer.append("GYROx");
//            Writer.append(",");
//            Writer.append("GYROy");
//            Writer.append(",");
//            Writer.append("GYROz");
//            Writer.append('\n');
//            Writer.flush();//??????
//        } catch (IOException e) {
//            Toast.makeText(MultipleMyosActivity.this, "IOexception", Toast.LENGTH_LONG).show();
//        }
//        //After initialzing all, trigger the int to begin record data
//        ready = 1;
//        text.setText("Data is recording....");
//    }

    private void hubInit(){
        Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Disable standard Myo locking policy. All poses will be delivered.
        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        final int attachingCount = 2;

        // Set the maximum number of simultaneously attached Myos to 2.
        hub.setMyoAttachAllowance(attachingCount);

        Log.i(TAG, "Attaching to " + attachingCount + " Myo armbands.");

        // attachToAdjacentMyos() attaches to Myo devices that are physically very near to the Bluetooth radio
        // until it has attached to the provided count.
        // DeviceListeners attached to the hub will receive onAttach() events once attaching has completed.
        hub.attachToAdjacentMyos(attachingCount);

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);

        // Attach an adapter to the ListView for showing the state of each Myo.
        mAdapter = new MyoAdapter(this, attachingCount);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Writer1.flush();
            Writer1.close();
            Writer2.flush();
            Writer2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        // Shutdown the Hub. This will disconnect any Myo devices that are connected.
        Hub.getInstance().shutdown();
    }

    // This is a utility function implemented for this sample that maps a Myo to a unique ID starting at 1.
    // It does so by looking for the Myo object in mKnownMyos, which onAttach() adds each Myo into as it is attached.
    private String identifyMyo(Myo myo) {
        return myo.getArm().toString();
    }

    private int identifyMyoNum(Myo myo) {
        return mKnownMyos.indexOf(myo) + 1;
    }

    private class MyoAdapter extends ArrayAdapter<String> {

        public MyoAdapter(Context context, int count) {
            super(context, android.R.layout.simple_list_item_1);

            // Initialize adapter with items for each expected Myo.
            for (int i = 0; i < count; i++) {
                add(getString(R.string.waiting_message));
            }
        }

        public void setMessage(Myo myo, String message) {
            // identifyMyo returns IDs starting at 1, but the adapter indices start at 0.
            int index = identifyMyoNum(myo) - 1;

            // Replace the message.
            remove(getItem(index));
            insert(message, index);
        }
    }

    protected FileWriter MyoConnectWriter(Myo myo) {
        String indexMyo = identifyMyo(myo);
        if (indexMyo == "LEFT") {
            return Writer1;
        } else return Writer2;
    }

    // specific method for writing CSV file.
    protected void writeCSV(long timestamp, Object[] buffer, FileWriter orientationWriter) {
        try {
            int i;
            buffer[0] = timestamp;
            for(i = 0; i < buffer.length - 1; i++){
                orientationWriter.append(buffer[i].toString());
                orientationWriter.append(",");
            }
            orientationWriter.append(buffer[buffer.length-1].toString());
            orientationWriter.append('\n');
            orientationWriter.flush();
        } catch (IOException e) {
            Toast.makeText(this, "IOexception", Toast.LENGTH_LONG).show();
        }
    }

    @AfterViews
    protected void attachViews() {
        mLeftLiveData = new LiveData(mPlotLeft);
        mRightLiveData = new LiveData(mPlotRight);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.plot_choices_array_myo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPlotChoice.setAdapter(adapter);
        mPlotChoice.setOnItemSelectedListener(new SpinnerPlotChoiceListener(this, mLeftLiveData, mRightLiveData) {
        });
        Toast.makeText(this,count+"",Toast.LENGTH_SHORT).show();
    }

}


