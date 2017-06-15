package andrej.jelic.attendance;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;

public class Started_at_time extends Activity {

    private static final String TAG = "MainActivity";
    public static final String PREFS_NAME = "PrefsFile";
    private static final String TAG_STARTED_AT_TIME_FRAGMENT = "started_at_time_fragment";
    private static final String TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT = "active_student_present_fragment";

    private static final int REQUEST_CODE_FINISHED = 2;
    private static long ALARM_TIME;
    private int end_hour;
    private int end_min;
    private boolean connected = false;

    private BluetoothAdapter mBluetoothAdapter;
    private AlarmManager mAlarmManager;
    private FragmentManager fm;
    private PendingIntent mFinishAtTimePendingIntent;
    private Intent mFinishAtTimeIntent;
    SharedPreferences preferences;
    private DatabaseHandler db;
    private StartedAtTimeFragment startedAtTimeFragment;
    private ActiveStudentPresentFragment activeStudentPresentFragment;
    private ActionBar.Tab tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        db = new DatabaseHandler(this);

        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowTitleEnabled(false);
        fm = getFragmentManager();

        if (savedInstanceState == null) {

            startedAtTimeFragment = new StartedAtTimeFragment();
            activeStudentPresentFragment = new ActiveStudentPresentFragment();

            preferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            end_hour = preferences.getInt("End hour", 0);
            end_min = preferences.getInt("End minutes", 0);

            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            mFinishAtTimeIntent = new Intent(this, AlarmReceiver.class);
            mFinishAtTimeIntent.putExtra("id", REQUEST_CODE_FINISHED);
            mFinishAtTimePendingIntent = PendingIntent.getBroadcast(this, 0, mFinishAtTimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            ALARM_TIME = time_to_millis(end_hour, end_min);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, ALARM_TIME, mFinishAtTimePendingIntent);

            addFirstTab(bar);
            addSecondTab(bar);

        } else {
            startedAtTimeFragment = (StartedAtTimeFragment) fm.getFragment(savedInstanceState, TAG_STARTED_AT_TIME_FRAGMENT);
            addFirstTab(bar);

            activeStudentPresentFragment = (ActiveStudentPresentFragment) fm.getFragment(savedInstanceState, TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT);
            if (activeStudentPresentFragment != null) {
                addSecondTab(bar);
            } else {
                activeStudentPresentFragment = new ActiveStudentPresentFragment();
                addSecondTab(bar);
            }
        }


    }

    private void addSecondTab(ActionBar bar) {
        tab = bar.newTab().setText(R.string.tab2).setTabListener(new TabListener<StudentPresentFragment>(this, activeStudentPresentFragment, TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT));
        bar.addTab(tab);
    }

    private void addFirstTab(ActionBar bar) {
        tab = bar.newTab().setText(R.string.tab1).setTabListener(new TabListener<StartedAtTimeFragment>(this, startedAtTimeFragment, TAG_STARTED_AT_TIME_FRAGMENT));
        bar.addTab(tab);
    }

    public long time_to_millis(int hour, int min) {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);

        c.set(year, month, day, hour, min);
        return c.getTimeInMillis();
    }

    public void setBooleanConnected(boolean booleanConnected) {
        connected = booleanConnected;
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
            startActivity(discoverableIntent);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        startedAtTimeFragment = (StartedAtTimeFragment) fm.findFragmentByTag(TAG_STARTED_AT_TIME_FRAGMENT);
        if (startedAtTimeFragment != null)
            fm.putFragment(outState, TAG_STARTED_AT_TIME_FRAGMENT, startedAtTimeFragment);

        activeStudentPresentFragment = (ActiveStudentPresentFragment) fm.findFragmentByTag(TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT);
        if (activeStudentPresentFragment != null)
            fm.putFragment(outState, TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT, activeStudentPresentFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_started_at_time, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem discoverable = menu.findItem(R.id.discoverable_started_at_time);
        MenuItem disconnect = menu.findItem(R.id.disconnect_started_at_time);

        if (mBluetoothAdapter.isEnabled()) discoverable.setEnabled(true);

        if (connected) disconnect.setEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.discoverable_started_at_time: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
              //  mBluetoothAdapter.disable();
                return true;
            }
            case R.id.end_session_started_at_time:
                db.clearActiveStudents();
                Intent intent = new Intent(this, Finished.class);
                startActivity(intent);
                mAlarmManager.cancel(mFinishAtTimePendingIntent);
                finish();
                return true;

            case R.id.cancel_session_started_at_time:
                db.clearActiveStudents();
                db.clearAllStudents();
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                startedAtTimeFragment.Stop();
                mAlarmManager.cancel(mFinishAtTimePendingIntent);
               // mBluetoothAdapter.disable();
                finish();
                return true;

            case R.id.disconnect_started_at_time:
                startedAtTimeFragment.Restart();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
