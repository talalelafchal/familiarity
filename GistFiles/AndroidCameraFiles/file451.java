package com.khushbu.phonesilencer.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.khushbu.phonesilencer.AppConstants;
import com.khushbu.phonesilencer.HomeFragment;
import com.khushbu.phonesilencer.LocationsUpdateService;
import com.khushbu.phonesilencer.R;
import com.khushbu.phonesilencer.adapter.NavDrawerListAdapter;
import com.khushbu.phonesilencer.model.NavDrawerItem;

import java.util.ArrayList;

/**
 * This class represents main home screen which has different fragments.
 */
public class MenuActivity extends AppCompatActivity {

	private static final String TAG = "MenuActivity";

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private int selectedPosition = 0;


	/**
	 * Messenger for communicating with the service.
	 */
	public Messenger mMessengerService = null;
	boolean mBound = false;

	/**
	 * Target we publish for service to send messages to MyHandler.
	 */
	final Messenger mMessenger = new Messenger(new MyHandler());
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@SuppressWarnings("ResourceType")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slider_menu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

        /* adding nav drawer items to array */
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Manage Zones
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Settings
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Exit
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// Setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(this, navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

			/**
			 * Called when a drawer has settled in a completely open state.
			 */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(getString(R.string.title_menu));
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			/**
			 * Called when a drawer has settled in a completely closed state.
			 */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	@Override
	public void onStart() {

		super.onStart();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();

		// Bind to LocalService
		Intent intent = new Intent(this, LocationsUpdateService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Menu Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.khushbu.phonesilencer.ui/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Menu Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.khushbu.phonesilencer.ui/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);

		// Unbind from the service
		if (mBound) {

			if (mMessengerService != null) {

				try {

					Message msg = Message.obtain(null, AppConstants.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mMessengerService.send(msg);
					Log.d(TAG, "Unregister client message sent");

				} catch (RemoteException e) {
					// There is nothing special we need to do if the service
					// has crashed.
				}
			}

			unbindService(mConnection);
			mBound = false;
		}
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.disconnect();
	}

	/**
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {

			mMessengerService = new Messenger(service);
			mBound = true;

			// We want to monitor the service for as long as we are connected to it.
			try {

				Message msg = Message.obtain(null, AppConstants.MSG_REGISTER_CLIENT);
				msg.replyTo = mMessenger;
				mMessengerService.send(msg);
				Log.d(TAG, "Register client message sent");

			} catch (RemoteException e) {
				// In this case the service has crashed before we could even
				// do anything with it; we can count on soon being
				// disconnected (and then reconnected if it can be restarted)
				// so there is no need to do anything here.
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mMessengerService = null;
			mBound = false;
		}
	};

	/**
	 * Handler of incoming messages from clients.
	 */
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			Log.d(TAG, "Handle Message, msg : " + msg.what);

			switch (msg.what) {

				case AppConstants.MSG_ENTERED_IN_SZ:

					if (getFragmentManager().findFragmentByTag("HomeFragment") != null) {
						((HomeFragment) getFragmentManager().findFragmentByTag("HomeFragment")).updateUI();
					}
					break;

				case AppConstants.MSG_LOCATION_REMOVED:

					if (getFragmentManager().findFragmentByTag("HomeFragment") != null) {
						((HomeFragment) getFragmentManager().findFragmentByTag("HomeFragment")).updateUI();
					}

					if (getFragmentManager().findFragmentByTag("ZoneListFragment") != null) {
						((ZoneListFragment) getFragmentManager().findFragmentByTag("ZoneListFragment")).refreshView();
					}
					break;

				default:
					super.handleMessage(msg);
			}
		}
	}

	/**
	 * Slide menu item click listener
	 */
	private class SlideMenuClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Displaying fragment view for selected nav drawer list item
	 */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		String tag = null;

		switch (position) {
			case 0:
				fragment = new HomeFragment();
				tag = "HomeFragment";
				break;
			case 1:
				fragment = new ZoneListFragment();
				tag = "ZoneListFragment";
				break;
			case 2:
				fragment = new Settings();
				tag = "Settings";
				break;
			case 3:
				// update selected item and title, then close the drawer
				mDrawerList.setItemChecked(position, true);
				mDrawerList.setSelection(position);
				mDrawerLayout.closeDrawer(mDrawerList);
				showExitAlert();
				break;
		}

		if (fragment != null) {

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, tag).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);

		} else {
			// error in creating fragment
			Log.e(TAG, "Error in creating fragment");
		}

		selectedPosition = position;
	}

	@Override
	public void onBackPressed() {

		if (selectedPosition != 0) {
			displayView(0);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


	/**
	 * This method is used to display alert dialog for exit.
	 */
	private void showExitAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle(getString(R.string.alert_dialog_title));

		// Setting Dialog Message
		alertDialog.setMessage(getString(R.string.exit_dialog_msg));

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (!mBound) {
					dialog.dismiss();
					finish();
					return;
				}

				// Create and send a message to the service, using a supported 'what' value
				Message restoreProfileMsg = Message.obtain(null, AppConstants.MSG_SET_PREVIOUS_PROFILE, 0, 0);
				Message stopServiceMsg = Message.obtain(null, AppConstants.MSG_STOP_SERVICE, 0, 0);

				try {

					mMessengerService.send(restoreProfileMsg);
					mMessengerService.send(stopServiceMsg);

				} catch (RemoteException e) {
					e.printStackTrace();
				}

				dialog.dismiss();
				finish();
			}
		});

		// Setting Negative "No" Button
		alertDialog.setNegativeButton(getString(R.string.btn_no), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
}
