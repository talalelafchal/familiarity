package jp.kickhost.eventnavi;

import java.util.ArrayList;

import jp.kickhost.localsearch.model.Event;
import jp.kickhost.localsearch.network.LocalSearchRestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends Activity {

        ActionBar mActionBar;
        EventListFragment mListFragment;
        EventMapFragment mMapFragment;

        /**
         * The serialization (saved instance state) Bundle key representing the
         * current tab position.
         */
        private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                mActionBar = getActionBar();
                mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                TabListener tabListener = new ActionBar.TabListener() {
                        @Override
                        public void onTabSelected(Tab tab, FragmentTransaction ft) {
                                if (tab.getText().equals("Map")) {
                                        if (mMapFragment == null) {
                                                mMapFragment = new EventMapFragment();
                                                ft.add(android.R.id.content, mMapFragment, "map");
                                        } else {
                                                ft.attach(mMapFragment);
                                        }

                                } else {
                                        if (mListFragment == null) {
                                                mListFragment = new EventListFragment();
                                                ft.add(android.R.id.content, mListFragment, "list");
                                        } else {
                                                ft.attach(mListFragment);
                                        }
                                }
                        }

                        @Override
                        public void onTabReselected(Tab tab, FragmentTransaction ft) {
                        }

                        @Override
                        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                                if (tab.getText().equals("Map")) {
                                        if (mMapFragment != null) {
                                                ft.detach(mMapFragment);
                                        }
                                } else {
                                        if (mListFragment != null) {
                                                ft.detach(mListFragment);
                                        }
                                }
                        }
                };

                mActionBar.addTab(mActionBar.newTab().setText("List").setTabListener(tabListener));
                mActionBar.addTab(mActionBar.newTab().setText("Map").setTabListener(tabListener));
                getEventList();
        }

        @Override
        public void onRestoreInstanceState(Bundle savedInstanceState) {
                // Restore the previously serialized current tab position.
                if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
                        getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
                }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
                // Serialize the current tab position.
                outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
        }

}