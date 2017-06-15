package com.chitchat.android.chitchat.adapters;

/**
 * Created by Rahul Chandra on 12/15/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chitchat.android.chitchat.views.CheeseListFragment;
import com.chitchat.android.chitchat.views.FriendsFragment;
import com.chitchat.android.chitchat.views.InboxFragment;
import com.chitchat.android.chitchat.views.MapFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public static FragmentManager fragmentManager;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return new CheeseListFragment();
            case 1:
                return new CheeseListFragment();
            case 2:
                return new MapFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3; // Show 3 total pages.
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "TAB 1";
            case 1:
                return "TAB 2";
            case 2:
                return "TAB 3";
        }
        return null;
    }

}