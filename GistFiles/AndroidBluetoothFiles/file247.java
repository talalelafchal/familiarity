package andrej.jelic.attendance;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;


public class Started_now extends Activity {

    private static final String TAG_STARTED_NOW_FRAGMENT = "started_now_fragment";
    private static final String TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT = "active_student_present_fragment";
    private static final int REQUEST_CODE_START = 1;
    private static final String TAG = "Started now ";

    private Intent mCancelAlarmIntent;
    private AlarmManager mAlarmManager;
    private PendingIntent mCancelAlarmIntentPendingIntent;
    private BluetoothAdapter mBluetoothAdapter;
    private DatabaseHandler db;
    private FragmentManager fm;
    private StartedNowFragment startedNowFragment;
    private ActiveStudentPresentFragment activeStudentPresentFragment;
    private ActionBar.Tab tab;
    private boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        db = new DatabaseHandler(this);

        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowTitleEnabled(false);
        fm = getFragmentManager();

        if (savedInstanceState != null) {
            startedNowFragment = (StartedNowFragment) fm.getFragment(savedInstanceState, TAG_STARTED_NOW_FRAGMENT);
            addFirstTab(bar);

            activeStudentPresentFragment = (ActiveStudentPresentFragment) fm.getFragment(savedInstanceState, TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT);
            if (activeStudentPresentFragment != null) {
                addSecondTab(bar);
            } else {
                activeStudentPresentFragment = new ActiveStudentPresentFragment();
                addSecondTab(bar);
            }

        } else {
            startedNowFragment = new StartedNowFragment();
            activeStudentPresentFragment = new ActiveStudentPresentFragment();

            addFirstTab(bar);
            addSecondTab(bar);

            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            mCancelAlarmIntent = new Intent(this, AlarmReceiver.class);
            mCancelAlarmIntent.putExtra("id", REQUEST_CODE_START);
            mCancelAlarmIntentPendingIntent = PendingIntent.getBroadcast(this, 0, mCancelAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mAlarmManager.cancel(mCancelAlarmIntentPendingIntent);
        }
    }

    private void addSecondTab(ActionBar bar) {
        tab = bar.newTab().setText(R.string.tab2).setTabListener(new TabListener<StudentPresentFragment>(this, activeStudentPresentFragment, TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT));
        bar.addTab(tab);
    }

    private void addFirstTab(ActionBar bar) {
        tab = bar.newTab().setText(R.string.tab1).setTabListener(new TabListener<StartedNowFragment>(this, startedNowFragment, TAG_STARTED_NOW_FRAGMENT));
        bar.addTab(tab);
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        startedNowFragment = (StartedNowFragment) fm.findFragmentByTag(TAG_STARTED_NOW_FRAGMENT);
        fm.putFragment(outState, TAG_STARTED_NOW_FRAGMENT, startedNowFragment);

        activeStudentPresentFragment = (ActiveStudentPresentFragment) fm.findFragmentByTag(TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT);
        if (activeStudentPresentFragment != null)
            fm.putFragment(outState, TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT, activeStudentPresentFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_started_now, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem discoverable = menu.findItem(R.id.discoverable_started_now);
        MenuItem disconnect = menu.findItem(R.id.disconnect_started_now);

        if (!mBluetoothAdapter.isEnabled()) discoverable.setEnabled(true);

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

            case R.id.cancel_session_started_now:
                db.clearActiveStudents();
                db.clearAllStudents();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                startedNowFragment.Stop();
                //mBluetoothAdapter.disable();
                finish();
                return true;

            case R.id.discoverable_started_now:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;

            case R.id.disconnect_started_now:
                startedNowFragment.Restart();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
    }

}
