package com.trumpstuff.mapdemo;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created on 04.10.2014.
 */
public class CustomPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomPreferenceFragment customPreferenceFragment = new CustomPreferenceFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content,customPreferenceFragment).commit();
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
    }
}
