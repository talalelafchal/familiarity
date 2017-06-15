import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public enum Screen {
        BOOKMARKS,
        TAGS,
        SETTINGS,
        PROFILE
    }

    public static class FragmentInfo {
        public String name;
        public String fragmentName;

        FragmentInfo(String name, String classPath) {
            this.name = name;
            this.fragmentName = classPath;
        }
    }

    private static final Map<Screen, FragmentInfo> fragments = Collections.unmodifiableMap(
            new HashMap<Screen, FragmentInfo>() {{
                put(Screen.BOOKMARKS, new FragmentInfo("Bookmarks", "bm.bookmark_manager.view.bm_list.BmListView"));
                put(Screen.TAGS, new FragmentInfo("Tags", "bm.bookmark_manager.view.tag_list.TagListView"));
            }});

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView(R.layout.activity__main, R.id.nav_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Open home (bookmark list)
        openFragment(Screen.BOOKMARKS);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideKeyboard();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            openFragment(Screen.BOOKMARKS);
        } else if (id == R.id.nav_tags) {
            openFragment(Screen.TAGS);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openFragment(Screen screen) {

        FragmentInfo fragmentInfo = fragments.get(screen);

        toolbar.setTitle(fragmentInfo.name);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentInfo.fragmentName);

        if (fragment == null) {
            fragment = Fragment.instantiate(this, fragmentInfo.fragmentName);
        }

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main, fragment, fragmentInfo.fragmentName);
        tx.commit();
    }
}