package com.webserveis.app.testpatternnavigation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class NavViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = NavViewActivity.class.getSimpleName();
    private static final String navDefaultNameFragment = FragmentA.class.getName();
    private static int navDefaultMenuItem = R.id.nav_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        onNavigationItemSelected(navigationView.getMenu().findItem(navDefaultMenuItem));

    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed()");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Log.d(TAG, "close Drawer");
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
                Log.d(TAG, "opBackStack()");
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment newFragment = null;

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            newFragment = FragmentA.newInstance(id, item.getTitle().toString());
        } else if (id == R.id.nav_gallery) {
            newFragment = FragmentB.newInstance(id, item.getTitle().toString());
        } else if (id == R.id.nav_slideshow) {
            newFragment = FragmentC.newInstance(id, item.getTitle().toString());
        } else if (id == R.id.nav_manage) {
            newFragment = FragmentD.newInstance(id, item.getTitle().toString());
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        if (newFragment != null) {
            openFragment(newFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void addFragment(Fragment newFragment) {
        Log.i(TAG, "addFragment() called with: newFragment = [" + newFragment + "]");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frame_container, newFragment);
        ft.commit();
    }

    private void replaceFragment(Fragment newFragment) {
        Log.i(TAG, "replaceFragment() called with: newFragment = [" + newFragment + "]");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, newFragment);
        ft.addToBackStack(newFragment.getClass().getName());
        ft.commit();
    }

    private void openFragment(Fragment newFragment) {

        String newFragmentName = newFragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();
        Fragment containerFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);

        if (containerFragment == null) {
            addFragment(newFragment);
        } else {

            if (!containerFragment.getClass().getName().equalsIgnoreCase(newFragmentName)) {

                if (newFragmentName.equals(navDefaultNameFragment)) {
                    Log.w(TAG, "Reset backstack fragments: ");
                    fm.popBackStack(0,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
/*                    boolean fragmentPopped = fm.popBackStackImmediate(newFragmentName, 0);
                    if (!fragmentPopped) */replaceFragment(newFragment);
                }

            }

        }

    }

/*       private void replaceFragment(Fragment newFragment) {
        String backStateName = newFragment.getClass().getName();

        FragmentManager fm = getSupportFragmentManager();
        boolean fragmentPopped = fm.popBackStackImmediate(backStateName, 0);
        Log.d(TAG, "fragmentPopped = " + valueOf(fragmentPopped) + "name: " + backStateName);

        if (!fragmentPopped) {

            Log.v(TAG, "Add new fragment [" + backStateName + "]");
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right,
                    R.anim.slide_in_left, R.anim.slide_out_left);
            ft.replace(R.id.frame_container, newFragment, backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();

        }

        Log.d(TAG, "fragments count: " + fm.getBackStackEntryCount());
        for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
            Log.d(TAG, "fragment [" + String.valueOf(entry) + "] = " + fm.getBackStackEntryAt(entry).getName());
        }
    }


        public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }*/

}