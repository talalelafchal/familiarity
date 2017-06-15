package com.chocoy.mypunch;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    //IBinder
    MainService.MyBinder binder;


    //serviceConnection
    ServiceConnection serviceConnection = new  ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("rootView bindService: ", "onServiceConnected.");

            binder = (MainService.MyBinder) service;

            Log.i("Binder Random Int is ", "" + binder.getRandomInt());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("rootView bindService: ", "onServiceDisconnected.");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Log.i(MainActivity.class.toString(),"onCreate.");

        //bind service
        Intent intent = new Intent("android.intent.service.MAIN_SERVICE");

        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass

        //Unbind service MainService
        unbindService(serviceConnection);

        // Stop method tracing that the activity started during onCreate()
        android.os.Debug.stopMethodTracing();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */


        List<Switch> switchList = new ArrayList<Switch>();

        MainSwitchListener mainSwitchListener = new MainSwitchListener();


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            switchList.add((Switch)rootView.findViewById(R.id.switch1));
            switchList.add((Switch)rootView.findViewById(R.id.switch2));
            switchList.add((Switch)rootView.findViewById(R.id.switch3));
            switchList.add((Switch)rootView.findViewById(R.id.switch4));

            for (Switch aSwitch : switchList) {
                aSwitch.setOnCheckedChangeListener(mainSwitchListener);
            }

            return rootView;
        }

        class MainSwitchListener implements Switch.OnCheckedChangeListener{

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //keep buttonView to be the only checked one
                if(isChecked){
                    for (Switch aSwitch : switchList) {
                        if (aSwitch.getId() != buttonView.getId() || aSwitch.isChecked()){
                            aSwitch.setChecked(false);
                        }
                        if(aSwitch.getId() == buttonView.getId()){

                            Intent intent = new Intent("android.intent.service.PUSH_SERVICE");
                            intent.putExtra("executeDateLongValue",switchList.indexOf(aSwitch));

                            getActivity().startService(intent);
                        }
                    }
                }
            }
        }
    }
}
