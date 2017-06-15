package andrej.jelic.attendance;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;

public class TabListener<T extends Fragment> implements ActionBar.TabListener {

    private static final String TAG = "TabListener";
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;

    public TabListener(Activity activity, Fragment fragment, String tag ){
        this.mActivity = activity;
        this.mFragment = fragment;
        this.mTag = tag;

    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        Log.e(TAG, "onTabSelected called");

        Fragment fragment;
        FragmentManager fm = mActivity.getFragmentManager();
        fragment = fm.findFragmentByTag(mTag);
        if (fragment != null) {
            Log.e(TAG, "fragment nije nula");
            mFragment = fragment;
            ft.attach(mFragment);
        } else ft.replace(android.R.id.content, mFragment, mTag);

    }
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (null != mFragment)
            ft.detach(mFragment);
        Log.e(TAG, "onTab Unselected called");

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.e(TAG, "onTab Reselected called");

        ft.attach(mFragment);
    }
}
