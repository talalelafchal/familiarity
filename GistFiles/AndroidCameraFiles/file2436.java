package com.ztt.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by 123 on 14-11-13.
 */
public class NerdLauncherActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new NerdLauncherFragment();
    }
}
