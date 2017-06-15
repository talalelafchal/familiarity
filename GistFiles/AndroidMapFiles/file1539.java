import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Gerónimo Oñativia <geronimox@gmail.com>
 */
public class Analytics {
    private final static long DEFAULT_DELAY_MILLISECONDS = 2000;
    private final static String TAG = Analytics.class.getName();

    private static Analytics instance;
    private Tracker tracker;
    private HashMap<String, TrackRunnable> runnableHashMap = new HashMap<>();
    private Handler handler = new Handler();

    private Analytics(Application application, String trackingId, boolean dryRun) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(application);
        analytics.setDryRun(dryRun);
        tracker = analytics.newTracker(trackingId);
        tracker.setSessionTimeout(10);
    }

    public static Analytics instance(Application application, String trackingId, boolean dryRun) {
        if (instance == null) instance = new Analytics(application, trackingId, dryRun);
        return instance;
    }

    protected static Analytics instance() {
        return instance;
    }

    public void sendEvent(String category, String action) {
        sendEvent(category, action, null, null);
    }

    public void sendEvent(String category, String action, String label) {
        sendEvent(category, action, label, null);
    }

    public void sendEvent(String category, String action, String label, Long value) {
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder.setCategory(category).setAction(action);
        if (label != null) eventBuilder.setLabel(label);
        if (value != null) eventBuilder.setValue(value);
        tracker.send(eventBuilder.build());
    }

    public void trackImmediate(String screenName) {
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void trackDelayed(String screenName) {
        trackDelayed(screenName, null, DEFAULT_DELAY_MILLISECONDS);
    }

    public void trackDelayed(String screenName, String key) {
        trackDelayed(screenName, key, DEFAULT_DELAY_MILLISECONDS);
    }

    public void trackDelayed(String screenName, long delayMilliseconds) {
        trackDelayed(screenName, null, delayMilliseconds);
    }

    public void trackDelayed(String screenName, String key, long delayMilliseconds) {
        String mapKey = (key == null || key.trim().length() <= 0) ? screenName : key;
        TrackRunnable runnable = runnableHashMap.get(mapKey);
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            Log.v(TAG, "Refreshing runnable timeout: " + mapKey);
        } else {
            runnable = new TrackRunnable(screenName, mapKey);
            runnableHashMap.put(mapKey, runnable);
            Log.v(TAG, "Adding runnable: " + mapKey);
        }
        handler.postDelayed(runnable, delayMilliseconds);
    }

    public void abortTrackDelayedByKey(String key) {
        Runnable runnable = runnableHashMap.remove(key);
        if (runnable != null) handler.removeCallbacks(runnable);
    }

    public void abortAllTrackDelayed() {
        Log.v(TAG, "Removing all track delayed runnables");
        Iterator<TrackRunnable> it = runnableHashMap.values().iterator();
        while (it.hasNext()) {
            handler.removeCallbacks(it.next());
            it.remove();
        }
    }

    public Tracker getTracker() {
        return tracker;
    }

    private static class TrackRunnable implements Runnable {
        public String key;
        public String screenName;

        private TrackRunnable(String screenName, String key) {
            this.screenName = screenName;
            this.key = key;
        }

        @Override
        public void run() {
            Analytics analytics = Analytics.instance();
            analytics.runnableHashMap.remove(key);
            analytics.trackImmediate(screenName);
        }
    }
}