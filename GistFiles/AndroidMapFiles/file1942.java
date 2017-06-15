import android.os.SystemClock;
import android.view.View;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A Debounced OnClickListener
 * Rejects clicks that are too close together in time.
 * This class is safe to use as an OnClickListener for multiple views, and will debounce each one separately.
 * http://stackoverflow.com/questions/16534369/avoid-button-multiple-rapid-clicks
 */
public abstract class OnDebouncedClickListener implements View.OnClickListener {
    private static final int DEFAULT_MINIMUM_INTERVAL = 222; // ms
    private final long minimumInterval;
    private Map<View, Long> lastClickMap;

    /**
     * Implement this in your subclass instead of onClick
     * @param v The view that was clicked
     */
    public abstract void onDebouncedClick(View v);

    public OnDebouncedClickListener() {
        this(DEFAULT_MINIMUM_INTERVAL);
    }

    /**
     * Constructor
     * @param minimumIntervalMsec The minimum allowed time between clicks -
     *                            any click sooner than this after a previous click will be rejected.
     */
    public OnDebouncedClickListener(long minimumIntervalMsec) {
        this.minimumInterval = minimumIntervalMsec;
        this.lastClickMap = new WeakHashMap<View, Long>();
    }

    @Override public void onClick(View clickedView) {
        Long previousClickTimestamp = lastClickMap.get(clickedView);
        long currentTimestamp = SystemClock.uptimeMillis();

        lastClickMap.put(clickedView, currentTimestamp);
        if(previousClickTimestamp == null ||
                (currentTimestamp - previousClickTimestamp.longValue() > minimumInterval)) {
            onDebouncedClick(clickedView);
        }
    }
}
