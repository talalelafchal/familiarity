package com.example.summer.newapp;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    OblastyFragment fragment;
    MosOblFragment MoFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragment = new OblastyFragment();
        MoFragment = new MosOblFragment();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getFragmentManager().beginTransaction()
                    //.remove(new GameFragment())
            //.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right)
                    .replace(R.id.content_main, new OblastyFragment())
                    .addToBackStack(null)
                    .setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commit();

        }else if (id == R.id.nav_oblasty) {
            getFragmentManager().beginTransaction()
            //.remove(new OblastyFragment())
            .addToBackStack(null)
            .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right)
            //.replace(R.id.content_main,new GameFragment())
            .commit();
        } else if (id == R.id.nav_poisk) {

        } else if (id == R.id.nav_soveti) {

        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT,
                    "https://drive.google.com/open?id=0Bw4YLHMMkZtVVDV1QzM2eEI2VUU");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ElZi");
            startActivity(Intent.createChooser(sharingIntent, "Поделиться ссылкой"));
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    }

