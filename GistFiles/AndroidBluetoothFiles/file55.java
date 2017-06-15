package andrej.jelic.attend;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_NORMAL;


public class LoginActivity extends ActionBarActivity {

    private int to_show;
    private FragmentManager fragmentManager;

    private static final String TAG = "LoginActivity";
    public static final String PREFS_NAME = "PrefsFile";

    SharedPreferences prefs;
    private boolean isLargeTabletLand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        to_show = prefs.getInt("Fragment_to_show", 0);
        Log.e(TAG, "broj fragmenta  " + to_show);

        setContentView(R.layout.activity_login);

        if (findViewById(R.id.frame2) != null) {
            fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.frame2, new EnterData());
            transaction.commit();
            isLargeTabletLand = true;
        }

        setLayout();
    }

    /*Napravljeno da odmah uz to sto postavlja layout, postavi odmah i fragmente koji su bili
      aktivni u trenutku kada se dogodi ConfigurationChange */
    private void setLayout() {

        if (to_show == 1) {
            fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.frame, new Warning());
            transaction.commit();

        } else if (to_show == 2) {
            if (isLargeTabletLand) {

                fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.frame, new Warning());
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.frame, new Warning());
                transaction.addToBackStack(null);
                transaction.commit();

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame, new EnterData());
                transaction.commit();
            }
        } else {
            if (isLargeTabletLand) {
                fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.frame, new Warning());
                transaction.commit();
            } else {
                fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.frame, new Login());
                transaction.commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //make addToBackStack work
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //ako se nije desilo change configuration, postavimo da aplikacija pocinje normalno od prvog fragmenta
    @Override
    protected void onDestroy() {
        super.onDestroy();

        boolean changing = this.isChangingConfigurations();
        if (!changing) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("Fragment_to_show", 0);
            editor.commit();
            to_show = prefs.getInt("Fragment_to_show", 0);
            Log.e(TAG, "broj fragmenta na kraju " + to_show);
        }

    }

}
