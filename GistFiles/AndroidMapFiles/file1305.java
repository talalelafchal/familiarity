package tr.edu.gyte.android.cellinfogsm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.telephony.CellIdentityGsm;
import android.telephony.CellInfoGsm;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "CELLINFO";
    private int mInterval = 1000; // 1 seconds by default, can be changed later
    private Handler mHandler;

    private LogItem mLogItem;


    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;


    // LOGGER VARIABLES
    private String mLogFileName;
    private static final String FOLDER = "cellinfogsm";
    private static final File EXTERNAL_STORAGE_DIR = Environment.getExternalStorageDirectory();
    private File mLogFile;
    private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    // UI VARIABLES
    private ArrayList<HashMap<String, String>> mCellInfoList;
    private SimpleAdapter mAdapter;
    private boolean isAdapterSet = false;
    private String mPrefix = "";
    private static EditText mPrefixET;
    private static ListView mCellInfoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        mHandler = new Handler();

        mCellInfoListView = (ListView) findViewById(R.id.cell_info_list);

        mTelephonyManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new PhoneStateListener();

        mTelephonyManager.listen(mPhoneStateListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS|
                PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR |
                PhoneStateListener.LISTEN_CALL_STATE |
                PhoneStateListener.LISTEN_CELL_LOCATION |
                PhoneStateListener.LISTEN_DATA_ACTIVITY |
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
                PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR |
                PhoneStateListener.LISTEN_SERVICE_STATE
        );
        mCellInfoList = new ArrayList<HashMap<String, String>>();
        mAdapter = new SimpleAdapter(getBaseContext(), mCellInfoList,
                R.layout.row, new String[] {"cid", "dbm"},new int[] {R.id.CID, R.id.DBM});
        openNewLogFile();
        startRepeatingTask();
    }

    void updateCurrentCellInfoList() {
        List<CellInfo> cellInfos = mTelephonyManager.getAllCellInfo();
        HashMap<String, String> map;
        LogItem logitem;
        if(cellInfos != null) {
            mCellInfoList.clear();
            int cellCount = cellInfos.size();
            mLogItem = new LogItem();
            mLogItem.mPrefix = mPrefix;

            for(CellInfo cellInfo : cellInfos)
            {
                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();

                CellItem cellItem = new CellItem();
                cellItem.rssi = cellInfoGsm.getCellSignalStrength().getDbm();
                cellItem.lac = cellInfoGsm.getCellIdentity().getLac();
                cellItem.mcc = cellInfoGsm.getCellIdentity().getMcc();
                cellItem.mnc = cellInfoGsm.getCellIdentity().getMnc();
                cellItem.asu = cellInfoGsm.getCellSignalStrength().getAsuLevel();
                cellItem.level = cellInfoGsm.getCellSignalStrength().getLevel();
                cellItem.cid = cellIdentity.getCid();
                mLogItem.mCellItems.add(cellItem);

                map = new HashMap<String, String>();
                map.put("cid", String.valueOf(cellItem.cid));
                map.put("dbm", String.valueOf(cellItem.rssi));
                mCellInfoList.add(map);
            }
        }


        TelephonyManager telephonyManager = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        List<NeighboringCellInfo> cells = telephonyManager
                .getNeighboringCellInfo();
        int phoneType = telephonyManager.getPhoneType();
        if (cells != null) {
            for (NeighboringCellInfo c : cells) {

                int dBm = c.getRssi();
                if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
                    dBm = dBm * 2 - 113;
                }
                Log.e("TESTGSM",""+c.getLac()+"|"+c.getCid()+"|"+c
                        .getRssi());
            }
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            updateCurrentCellInfoList();
            if(mPrefixET != null)
                mPrefix = mPrefixET.getText().toString();
            if(mCellInfoListView != null && !isAdapterSet) {
                mCellInfoListView.setAdapter(mAdapter);
                isAdapterSet = true;
            }
            if(mAdapter != null)
                mAdapter.notifyDataSetChanged();
            if(mCellInfoList.size() > 0)
                appendLog(mCellInfoList);
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_save:
                openNewLogFile();
                Toast.makeText(this,"Creating New File",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_share:
                email(this,mLogFile.getParent());
                return true;
            case R.id.action_delete:
                deleteLogs();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteLogs(){
        new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle("Deleting Logs")
                .setMessage("Do you really want to delete logs?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mLogFile != null && mLogFile.exists()) {
                            File logDir = new File(mLogFile.getParent());
                            if (logDir.exists() && logDir.isDirectory()) {
                                File logFiles[] = logDir.listFiles();
                                Log.d("Files", "Size: " + logFiles.length);
                                for (File file : logFiles) {
                                    file.delete();
                                }
                                Toast.makeText(getApplicationContext(), "Logs were deleted!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mCellInfoListView = (ListView) rootView.findViewById(R.id.cell_info_list);
            mPrefixET = (EditText) rootView.findViewById(R.id.prefix);
            return rootView;
        }
    }

    public void openNewLogFile() {
        Date date = new Date();
        mLogFileName = DATEFORMAT.format(date);
        mLogFile = new File(EXTERNAL_STORAGE_DIR,FOLDER+"/"+mLogFileName+".log");
        if (!mLogFile.exists()) {
            try {
                new File(mLogFile.getParent()).mkdirs();
                mLogFile.createNewFile();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.i("CELLINFOGSM","New log file created: "+mLogFile.getAbsolutePath());
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(mLogFile, true));
            buf.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n","TIME","PREFIX","CID","RSSI","ASU","LAC","LEVEL","MCC","MNC"));
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendLog(ArrayList<HashMap<String,String>> list) {
        if (mLogFile != null && mLogFile.exists()) {
            try {
                //BufferedWriter for performance, true to set append to file flag

                BufferedWriter buf = new BufferedWriter(new FileWriter(mLogFile, true));
                Date date  = new Date();

                buf.append(String.format("%s,%s,%d",DATEFORMAT.format(date),mPrefix,mLogItem.mCellItems.size()));
                for(CellItem item : mLogItem.mCellItems)
                    buf.append(String.format(",%d,%d,%d,%d,%d,%d,%d",item.cid,item.rssi,item.asu,item.lac,item.level,item.mcc,item.mnc));
                buf.newLine();

                buf.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void email(Context context, String logPath) {
        //need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");

        Date date = new Date();
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Cell Logs "+ DATEFORMAT.format(date));

        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        //convert from paths to Android friendly Parcelable Uri's
        Log.d("Files", "Path: " + logPath);
        if(logPath == null) {
            Log.e(TAG,"Path is invalid, mail sending cancelled!");
            return;
        }

        File logDir = new File(logPath);
        if(logDir.exists() && logDir.isDirectory()) {
            File logFiles[] = logDir.listFiles();
            Log.d("Files", "Size: "+ logFiles.length);

            for (File file : logFiles) {
                Uri u = Uri.fromFile(file);
                uris.add(u);
            }
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
    }

    public class CellItem{
        public int rssi;
        public int lac;
        public int mcc;
        public int mnc;
        public int asu;
        public int level;
        public int cid;
    }

    public class LogItem {
        public String mPrefix;
        public ArrayList<CellItem> mCellItems = new ArrayList<CellItem>();
    }
}
