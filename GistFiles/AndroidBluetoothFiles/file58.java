package andrej.jelic.attendance;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class StartedNowFragment extends Fragment implements View.OnClickListener {

    public static final String PREFS_NAME = "PrefsFile";
    public static final String START_TIME = "Start time";
    private static final String TAG = "Started now fragment ";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService = null;
    private Calendar calendar = Calendar.getInstance();
    private DatabaseHandler db;
    private ProgressBar mProgressBar;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private int hour;
    private int min;
    private int lastState;
    private String mConnectedDeviceName = null;
    private String attendText;
    private String error;
    private String leaveText;
    private String startTimeString;
    private String attendTime;
    private boolean connected = false;

    private TextView mTitle;
    private TextView startTime;
    private TextView studentPresent;


    public StartedNowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (savedInstanceState == null) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            min = calendar.get(Calendar.MINUTE);
            writeToPrefs();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = new DatabaseHandler(getActivity());
        View v = init(inflater, container);
        return v;
    }

    private View init(LayoutInflater inflater, ViewGroup container) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        attendText = getResources().getString(R.string.Attend);
        leaveText = getResources().getString(R.string.Leave);
        error = getResources().getString(R.string.Error);

        View v = inflater.inflate(R.layout.fragment_started_now, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar_fragment_now);
        startTime = (TextView) v.findViewById(R.id.view_start_time_fragment_now);
        studentPresent = (TextView) v.findViewById(R.id.view_student_present_fragment_now);
        studentPresent.setText(Integer.toString(db.getActiveStudentsCount()));
        mTitle = (TextView) v.findViewById(R.id.title_fragment_now);

        Button endNow = (Button) v.findViewById(R.id.button_end_now_fragment_now);
        endNow.setOnClickListener(this);
        return v;

    }

    private void writeToPrefs() {

        preferences = this.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("Hour", hour);
        editor.putInt("Minutes", min);
        editor.apply();
    }

    private static String pad(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        } else return "0" + String.valueOf(c);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            startTimeString = savedInstanceState.getString(START_TIME);
            Log.e(TAG, "Start time u fragment activity created " + startTimeString);
        } else {
            preferences = this.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            hour = preferences.getInt("Hour", 0);
            min = preferences.getInt("Minutes", 0);
            startTimeString = String.valueOf(new StringBuilder().append(pad(hour)).append(" : ").append(pad(min)));
        }

        startTime.setText(startTimeString);

    }


    @Override
    public void onStart() {
        super.onStart();


        preferences = this.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        editor = preferences.edit();

        if (!mBluetoothAdapter.isEnabled()) {
            new Enable().execute();
            Log.e(TAG, "Enable blutut ");

        } else if (mChatService == null) {
            setupChat();
        } else {
            lastState = preferences.getInt("lastState", 1);
            mHandler.obtainMessage(MESSAGE_STATE_CHANGE, lastState, -1).sendToTarget();
            Log.e(TAG, "Last state " + lastState);
        }
    }


    private void setupChat() {
        mChatService = new BluetoothChatService(getActivity(), mHandler);
        Log.e(TAG, "Sad je pokrenut Chat service ");
        mChatService.start();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.button_end_now_fragment_now) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClass(getActivity(), Finished.class);
            startActivity(intent);
            mChatService.stop();
            db.clearActiveStudents();
            getActivity().finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(START_TIME, startTimeString);
        Log.e(TAG, "Start time u fragment saveAs instance state ");

    }

    public void Stop() {
        mChatService.stop();
    }

    public void Restart() {
        mChatService.stop();
        mChatService.start();
    }

    public void sendMessage(String message) {
        byte[] send = message.getBytes();
        Log.e(TAG, "Sending message " + message);
        mChatService.write(send);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.e(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            mTitle.setText(R.string.title_connected_to);
                            mTitle.append(mConnectedDeviceName);
                            connected = true;
                            editor.putInt("lastState", 3);
                            editor.apply();
                            ((Started_now) getActivity()).setBooleanConnected(connected);
                            getActivity().invalidateOptionsMenu();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            mTitle.setText(R.string.listening);
                            connected = false;

                            editor.putInt("lastState", 1);
                            editor.apply();
                            ((Started_now) getActivity()).setBooleanConnected(connected);
                            getActivity().invalidateOptionsMenu();
                            break;
                        case BluetoothChatService.STATE_NONE:
                            mTitle.setText(R.string.not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    Log.e(TAG, "mmOutStream write attendance" + writeBuf);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.e(TAG, "Reading message " + readMessage);
                    mTitle.setText(readMessage);

                    if (readMessage.length() > 0) {
                        if (readMessage.contains(attendText)) {
                            new signHimIn().execute(readMessage);
                        } else if (readMessage.contains(leaveText))
                            new deleteHim().execute(readMessage);
                    }

                    break;

                case MESSAGE_DEVICE_NAME:
                    // saveAs the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Log.d(TAG, "mConnectedDeviceName" + mConnectedDeviceName);
                  /*  Toast.makeText(getActivity(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();*/
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getActivity(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public class signHimIn extends AsyncTask<String, String, String> {

        private static final String TAG = "Sign him in: ";
        private String signed;
        private String alreadySigned;
        Calendar calendar = Calendar.getInstance();

        @Override
        protected void onPreExecute() {
            int attend_hour = calendar.get(Calendar.HOUR_OF_DAY);
            int attend_min = calendar.get(Calendar.MINUTE);
            attendTime = String.valueOf(new StringBuilder().append(pad(attend_hour)).append(" : ").append(pad(attend_min)));

            Log.e(TAG, "Sign him in pre execute " + attendTime);
            signed = getActivity().getResources().getString(R.string.signed_in);
            alreadySigned = getActivity().getResources().getString(R.string.already_signed);
        }

        @Override
        protected String doInBackground(String... params) {
            String readMessage = params[0];
            String student = readMessage.substring(7);
            Log.e(TAG, "Student sign in: " + student);

            boolean checkStudent = db.checkStudent(new Student(student));
            boolean checkActiveStudent = db.checkActiveStudent(new ActiveStudent(student));

            //Returns true if there is no student in the database
            if (checkStudent) {
                db.addStudent(new Student(student, attendTime, null));
            }
            if (checkActiveStudent) {
                db.addStudent(new ActiveStudent(student, attendTime));
                db.updateStudent(new Student(student, null));
                return signed;
            } else {
                return alreadySigned;
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            if (result.contains(signed)) {
                studentPresent.setText(Integer.toString(db.getActiveStudentsCount()));
                new Send().execute(attendText);
            }
            else if (result.contains(alreadySigned)) {
                new Send().execute(error);
            }
        }
    }

    private class Send extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            sendMessage(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mChatService.start();
        }
    }

    public class deleteHim extends AsyncTask<String, String, String> {

        private static final String TAG = "Delete him ";
        private String signed;
        private String leaveTime;
        private String noRecord;
        private String error;
        private boolean ok = true;
        Calendar calendar = Calendar.getInstance();


        @Override
        protected void onPreExecute() {
            int leave_hour = calendar.get(Calendar.HOUR_OF_DAY);
            int leave_min = calendar.get(Calendar.MINUTE);
            leaveTime = String.valueOf(new StringBuilder().append(pad(leave_hour)).append(" : ").append(pad(leave_min)));
            noRecord = getActivity().getString(R.string.noRecord);
            error = getActivity().getString(R.string.Error);

            Log.e(TAG, "Delete him pre execute " + leaveTime);
            signed = getActivity().getResources().getString(R.string.signed_out);
        }

        @Override
        protected String doInBackground(String... params) {
            String readMessage = params[0];
            String student = readMessage.substring(6);

            boolean checkStudent = db.checkStudent(new Student(student));
            boolean checkActiveStudent = db.checkActiveStudent(new ActiveStudent(student));

            //Returns true if there is no student in the database
            if (checkActiveStudent && checkStudent) {
                ok = false;
                return noRecord;
            } else if (!checkActiveStudent) {
                Log.e(TAG, "Student sign out: " + student);

                db.deleteStudent(new ActiveStudent(student, attendTime));
                db.updateStudent(new Student(student, leaveTime));

                return signed;
            } else {
                db.updateStudent(new Student(student, leaveTime));
                return signed;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();

            if (ok) {
                studentPresent.setText(Integer.toString(db.getActiveStudentsCount()));
                new Send().execute(leaveText);
            } else new Send().execute(error);
        }
    }


    public class Enable extends AsyncTask<String, Integer, String> {

        private static final String TAG = "Enabling bluetooth ";
        private String enabledBluetooth;

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            enabledBluetooth = getActivity().getResources().getString(R.string.bt_enabled);

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e(TAG, "Enable do in background");
            mBluetoothAdapter.enable();

            for (int i = 1; i < 100; i++) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress(i);
            }


            return enabledBluetooth;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                getActivity().invalidateOptionsMenu();
                setupChat();
            } else {
                Log.e(TAG, "Bluetooth not enabled");
                Toast.makeText(getActivity(), R.string.bluetooth_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "on Destroy ");

    }
}
