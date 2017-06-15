package andrej.jelic.attendance;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;


public class Finished extends Activity {

    public static final String PREFS_NAME = "PrefsFile";
    private static final String TAG_FINISHED_FRAGMENT = "finished_fragment";
    private static final String TAG_STUDENT_PRESENT_FRAGMENT = "student_present_fragment";
    private DatabaseHandler db;
    private FinishedFragment finishedFragment;
    private StudentPresentFragment studentPresentFragment;
    private BluetoothAdapter mBluetoothAdapter;
    private FragmentManager fm;
    private ActionBar.Tab tab;

    private boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       // mBluetoothAdapter.disable();

        db = new DatabaseHandler(this);
        finishedFragment = new FinishedFragment();
        studentPresentFragment = new StudentPresentFragment();

        Bundle args = new Bundle();
        args.putString("key", DatabasesContract.FeedFinishedDatabase.TABLE_NAME);
        studentPresentFragment.setArguments(args);

        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowTitleEnabled(false);
        fm = getFragmentManager();

        if (savedInstanceState != null) {

            finishedFragment = (FinishedFragment) fm.getFragment(savedInstanceState, TAG_FINISHED_FRAGMENT);
            addFirstTab(bar);

            studentPresentFragment = (StudentPresentFragment) fm.getFragment(savedInstanceState, TAG_STUDENT_PRESENT_FRAGMENT);
            if (studentPresentFragment != null) {
                addSecondTab(bar);
            } else {
                studentPresentFragment = new StudentPresentFragment();
                addSecondTab(bar);
            }

        } else {
            addFirstTab(bar);
            addSecondTab(bar);
        }


    }

    private void addSecondTab(ActionBar bar) {
        tab = bar.newTab().setText(R.string.tab2).setTabListener(new TabListener(this, studentPresentFragment, TAG_STUDENT_PRESENT_FRAGMENT));
        bar.addTab(tab);
    }

    private void addFirstTab(ActionBar bar) {
        tab = bar.newTab().setText(R.string.tab1).setTabListener(new TabListener(this, finishedFragment, TAG_FINISHED_FRAGMENT));
        bar.addTab(tab);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        fm.putFragment(outState, TAG_FINISHED_FRAGMENT, finishedFragment);

        studentPresentFragment = (StudentPresentFragment) fm.findFragmentByTag(TAG_STUDENT_PRESENT_FRAGMENT);
        if (studentPresentFragment != null)
            fm.putFragment(outState, TAG_STUDENT_PRESENT_FRAGMENT, studentPresentFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_finished, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.cancel_session:
                db.clearAllStudents();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;

            case R.id.finish_session:
                db.clearAllStudents();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
