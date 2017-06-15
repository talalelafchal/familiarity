package com.trumpstuff.mapdemo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created on 04.10.2014.
 */
public class CustomPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference pref = findPreference(s);
        if(pref != null && pref.getKey().equals("start_location")){
            updateLocationSetting(pref);
        }
        //Other preference handling
    }
    private void updateLocationSetting(Preference preference){
        if(((EditTextPreference) preference).getText().equals("Global")){
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(
                            getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("start_location_lat", "0");
            editor.putString("start_location_lng", "0");
            editor.apply();
            return;
        }
        if(Geocoder.isPresent()){
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                List<Address> addresses = geocoder.getFromLocationName(((EditTextPreference) preference).getText(), 1);
                if (addresses != null && addresses.size() > 0){
                    Address address = addresses.get(0);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("start_location_lat", String.valueOf(address.getLatitude()));
                    editor.putString("start_location_lng", String.valueOf(address.getLongitude()));
                    editor.apply();
                    Log.d("pref", sharedPreferences.getAll().toString());
                }
                else{
                    Toast.makeText(getActivity(), "Could not resolve Address: " + ((EditTextPreference) preference).getText(), Toast.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getActivity(),"Geocoder not available on your system",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
