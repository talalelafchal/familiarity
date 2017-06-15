package com.pany.android.myapp;

import android.app.Application;
import android.util.Log;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import java.util.HashMap;

/**
 * GlobalStateApplication state = ((GlobalStateApplication) getApplicationContext());
*/

public class GlobalStateApplication extends Application {
    // The Tag name for this class. Used for logging.
    private static final String TAG = "LLOOGG GlobalStateApplication";

    /**
     * Google Analytics SDK
     */
    public enum TrackerName {
        APP_TRACKER
    }
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    synchronized Tracker getTracker(TrackerName trackerId) {
        Log.d(TAG, "getTracker()");
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            // Global GA Settings
            // <!-- Google Analytics SDK V4 BUG20141213 Using a GA global xml freezes the app! Do config by coding. -->
            analytics.setDryRun(false);

            analytics.getLogger().setLogLevel(Logger.LogLevel.INFO);
            //analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

            // Create a new tracker
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.ga_tracker_config) : null;
            if (t != null) {
                t.enableAdvertisingIdCollection(true);
            }
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

}
