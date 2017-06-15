package com.alorma.universidad;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends Activity implements VersionChecker.VersionCheckerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "http://devel.tempos21.es/download/OSAM/ejemplo.json";

        VersionChecker vc = new VersionChecker(this, url, "ca", this);
        vc.check();
    }

    @Override
    public void onVersionAquired(String currentVersion) {
        Log.i("ALORMA", "Current: " + currentVersion);
    }

    @Override
    public void onVersionEnabled(String version) {
        Log.i("ALORMA", "Version: " + version + " enabled");
    }

    @Override
    public void onVersionDisabled(String version, String message) {
        Log.i("ALORMA", "Version: " + version + " disabled -> " + message);
    }

    @Override
    public void onVersionCheckError() {
        Log.i("ALORMA", "Check version fails");
    }
}
