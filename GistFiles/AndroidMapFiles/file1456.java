import android.os.SystemClock;
import android.view.View;

import java.util.Map;
import java.util.WeakHashMap;

public abstract class OnMultipleClickListener implements View.OnClickListener {
    private static final int DEFAULT_MAX_INTERVAL = 1000; // ms
    private int mClickIndex;
    private int mContinuousClickCount;
    private final long mMaxInterval;
    private Map<View, Long> mClickMap;

    /**
     * Implement this in your subclass instead of onClick
     * @param v The view that was clicked
     */
    public abstract void onMultipleClick(View v);

    public OnMultipleClickListener(int multiple) {
        this(multiple, DEFAULT_MAX_INTERVAL);
    }

    /**
     * Constructor
     * @param maxIntervalMsec The max allowed time between clicks, or else discard all previous clicks.
     */
    public OnMultipleClickListener(int multiple, long maxIntervalMsec) {
        mClickIndex = 0;
        mContinuousClickCount = multiple;
        mMaxInterval = maxIntervalMsec;
        mClickMap = new WeakHashMap<View, Long>();
    }

    @Override public void onClick(View clickedView) {
        Long previousClickTimestamp = mClickMap.get(clickedView);
        long currentTimestamp = SystemClock.uptimeMillis();

        mClickMap.put(clickedView, currentTimestamp);

        if (previousClickTimestamp == null) {
            // first click
            mClickIndex = 1;
        } else {
            // other click
            if ((currentTimestamp - previousClickTimestamp.longValue()) < mMaxInterval) {
                ++mClickIndex;
                if (mClickIndex >= mContinuousClickCount) {
                    mClickIndex = 0;
                    mClickMap.clear();
                    onMultipleClick(clickedView);
                }
            } else {
                // timeout
                mClickIndex = 1;
            }
        }
    }
}
