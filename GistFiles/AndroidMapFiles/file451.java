package com.example.mindxxxd.festivaling;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private AdView mAdView;
    private Fragment FR;
    private int menueID;
    private DrawerLayout drawer;
    private int toolbarTitleID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbarTitleID = R.string.app_name;
        menueID=R.menu.main;
        FR=new HomeFragment();
        refreshFragment();

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        refreshToolbar();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(menueID, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean newFragment = false;

        //noinspection SimplifiableIfStatement
        if (id == R.id.editCheckPoints) {
            FR = new ChecklistEdit();
            newFragment = true;
            menueID = R.menu.settings_checklist_edit;
        } else if (id == R.id.returnToChecklist){
            FR = new Checklist();
            menueID = R.menu.settings_checklist;
            newFragment = true;
        } else if (id == R.id.newCheckList){
            AlertDialog.Builder AD = new AlertDialog.Builder(this);
            AD.setTitle(R.string.newCheckList);
            AD.setMessage(R.string.confirmNewChecklist);
            AD.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FR = new Checklist();
                    ((Checklist) FR).newList = true;
                    menueID = R.menu.settings_checklist;
                    refreshFragment();
                }
            });
            AD.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FR = new Checklist();
                    menueID = R.menu.settings_checklist;
                    refreshFragment();
                }
            });
            AD.setIcon(android.R.drawable.ic_dialog_alert);
            AD.show();
        } else if (id == R.id.clearAll){
            AlertDialog.Builder AD = new AlertDialog.Builder(this);
            AD.setTitle(R.string.clearAll);
            AD.setMessage(R.string.clearAllConfirmation);
            AD.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FR = new Checklist();
                    ((Checklist) FR).newListBoolean=true;
                    refreshFragment();
                }
            });
            AD.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FR = new Checklist();
                    refreshFragment();
                }
            });
            AD.setIcon(android.R.drawable.ic_dialog_alert);
            AD.show();
        }

        refreshToolbar();
        if (newFragment){
            refreshFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshFragment(){
        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();
        FT.replace(R.id.FragmentContainerMain, FR);
        FT.commit();
    }

    public void refreshToolbar(){
        setSupportActionBar(toolbar);
        toolbar.setTitle(toolbarTitleID);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            FR = new HomeFragment();
            menueID = R.menu.main;
            toolbarTitleID = R.string.home;
        } else if (id == R.id.checkList) {
            FR = new Checklist();
            menueID = R.menu.settings_checklist;
            toolbarTitleID = R.string.checklist;
        } else if (id == R.id.nav_slideshow) {
            FR=null;
        } else if (id == R.id.nav_manage) {
            FR=null;
        } else if (id == R.id.nav_share) {
            FR=null;
        } else if (id == R.id.nav_send) {
            FR=null;
        } else FR=null;
        refreshFragment();
        drawer.closeDrawer(GravityCompat.START);
        refreshToolbar();
        return true;
    }
}

//Festival Checkliste (Nature One) with togleable ListPoints Usual / Not this Time / Never / Add possibility (Private/Public)

/*!!!
TODO
LocationOverview with Map;
Location Overview as List; toogleale list points
Private Timetable;
where are my group? remote GPS search.
Festival Chat (GPS or manual);
Clock;
Timer till the end;
Notizias;

*/