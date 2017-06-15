package com.mopub.mobileads;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.mopub.common.logging.MoPubLog;

import java.util.Map;

/**
 * This is a hack to defer the invalidation of an old ad until after the new ad is finished loading.
 *
 * @author jhansche
 * @see <a href="https://github.com/mopub/mopub-android-sdk/issues/171">The issue opened with MoPub</a>
 * @since 6/4/15
 */
public class MoPubView2 extends MoPubView {
    private CustomEventBannerAdapter mDeferredBannerAdapter;

    public MoPubView2(Context context) {
        super(context);
    }

    public MoPubView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void loadCustomEvent(String customEventClassName, Map<String, String> serverExtras) {
        if (mCustomEventBannerAdapter != null) {
            // the super class will immediately invalidate the current banner, so instead let's override that and defer the invalidate until after
            // the new banner is loaded.
            mDeferredBannerAdapter = mCustomEventBannerAdapter;
            mCustomEventBannerAdapter = null;
            MoPubLog.v("Loading a new banner; invalidation of old banner will be deferred: " + mDeferredBannerAdapter);
        }

        super.loadCustomEvent(customEventClassName, serverExtras);
    }

    @Override
    public void setAdContentView(View view) {
        if (mDeferredBannerAdapter != null && !mDeferredBannerAdapter.isInvalidated()) {
            MoPubLog.v("Deferred banner invalidation is happening now: " + mDeferredBannerAdapter);
            mDeferredBannerAdapter.invalidate();
            mDeferredBannerAdapter = null;
        }

        super.setAdContentView(view);
    }

    @Override
    public void destroy() {
        if (mDeferredBannerAdapter != null) {
            mDeferredBannerAdapter.invalidate();
        }

        super.destroy();
    }
}