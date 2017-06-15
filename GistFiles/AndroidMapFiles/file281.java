
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TabHost;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

            Resources res = getResources();

            TabHost tabs=(TabHost)rootView.findViewById(android.R.id.tabhost);
            tabs.setup();

            TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
            spec.setContent(R.id.tab1);
            spec.setIndicator("TAB1",
                    res.getDrawable(android.R.drawable.ic_btn_speak_now));
            tabs.addTab(spec);

            spec=tabs.newTabSpec("mitab2");
            spec.setContent(R.id.tab2);
            spec.setIndicator("TAB2",
                    res.getDrawable(android.R.drawable.ic_dialog_map));
            tabs.addTab(spec);

            tabs.setCurrentTab(0);

            tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                public void onTabChanged(String tabId) {
                    Log.i("AndroidTabsDemo", "Pulsada pestaña: " + tabId);
                }
            });

            return rootView;
        }
    }

}
