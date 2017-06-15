package com.example.fragmenttest;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.util.Log;

public class MainActivity extends Activity implements ActionBar.TabListener, FragmentCallbacks {

    public static final class PreserveFragment extends Fragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }
    }

  private static final String TAG = "MainActivity";

	enum TabType {
		PHOTOS, CART, PREFS
	};

	public HashMap<TabType, Stack<String>> backStacks;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	Fragment mLastFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "---- onCreate");


		setContentView(R.layout.main);

		// Set up the action bar to show tabs.
		ActionBar actionBar = getActionBar();
		//Util.d(actionBar, "actionBar");
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		getFragmentManager().enableDebugLogging(true);

		// Go back stacks from savedInstanceState.
		if(savedInstanceState != null){
			Log.d(TAG, "saveInstanceState is not null");

			// Read back stacks after orientation change.
			backStacks = (HashMap<TabType, Stack<String>>)savedInstanceState.getSerializable("backStacks");
			Log.d(TAG, backStacks.toString());

			// convert ArrayList => Stack
			Stack<String> photoStack = new Stack<String>();
			Stack<String> cartStack = new Stack<String>();
			Stack<String> prefsStack = new Stack<String>();

			List<String>photoList = backStacks.get(TabType.PHOTOS);
			List<String>cartList = backStacks.get(TabType.CART);
			List<String>prefsList = backStacks.get(TabType.PREFS);

			photoStack.addAll(photoList);
			backStacks.put(TabType.PHOTOS, photoStack);

			cartStack.addAll(cartList);
			backStacks.put(TabType.CART, cartStack);

			prefsStack.addAll(prefsList);
			backStacks.put(TabType.PREFS, prefsStack);
		}
		else{
			Log.d(TAG, "Initialize back stacks on first run");

			backStacks = new HashMap<TabType, Stack<String>>();
			backStacks.put(TabType.PHOTOS, new Stack<String>());
			backStacks.put(TabType.CART, new Stack<String>());
			backStacks.put(TabType.PREFS, new Stack<String>());

			mLastFragment = null;

            getFragmentManager().beginTransaction().add(new PreserveFragment(), "preserve").commit();
		}

		// Create tabs
		actionBar.addTab(actionBar.newTab().setTag(TabType.PHOTOS).setIcon(R.drawable.tab_icon_photo).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setTag(TabType.CART).setIcon(R.drawable.tab_icon_cart).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setTag(TabType.PREFS).setIcon(R.drawable.tab_icon_config).setTabListener(this));
	}

	@Override
	protected void onResume(){
		super.onResume();

		Log.d(TAG, "---- onResume");

		// Select proper stack.
		Tab tab = getActionBar().getSelectedTab();
		Stack<String> backStack = backStacks.get(tab.getTag());
		if (!backStack.isEmpty()) {
			// Restore topmost fragment
			String tag = backStack.peek();
			Fragment fragment = getFragmentManager().findFragmentByTag(tag);
			if (fragment.isDetached()) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.attach(fragment);
				ft.commit();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.d(TAG, "---- onPause");

		// Select proper stack
		Tab tab = getActionBar().getSelectedTab();
		Stack<String> backStack = backStacks.get(tab.getTag());
		if (!backStack.isEmpty()) {
			// Detach topmost fragment otherwise it will not be correctly
			// displayed
			// after orientation change
			String tag = backStack.peek();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.addToBackStack(null);
			Fragment fragment = getFragmentManager().findFragmentByTag(tag);
			ft.detach(fragment);
			ft.commit();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		Log.d(TAG, "---- onSaveInstanceState");

		// Serialize the current tab position.
		// Save selected tab and all back stacks
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				 .getSelectedNavigationIndex());
		outState.putSerializable("backStacks", backStacks);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		Log.d(TAG, "---- onRestoreInstanceState");

		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "---- onBackPressed");

		// Select proper stack
		Tab tab = getActionBar().getSelectedTab();
		Stack<String> backStack = backStacks.get(tab.getTag());
		String tag = backStack.pop();
		if (backStack.isEmpty()) {
			this.finish();
		} else {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			Fragment fragment = getFragmentManager().findFragmentByTag(tag);

			// Animate return to previous fragment
			ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

			// Remove topmost fragment from back stack and forget it
			ft.remove(fragment);
			showFragment(backStack, ft);
			ft.commit();
		}
	}


	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);

		Log.d(TAG, "---- onActivityResult requestCode:" + requestCode);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		Log.d(TAG, "---- onTabSelected");

		// Select proper stack
		Stack<String> backStack = (Stack<String>)backStacks.get(tab.getTag());

		if (backStack.isEmpty()) {
			// If it is empty instantiate and add initial tab fragment
			Fragment fragment;
			switch ((TabType) tab.getTag()) {
			case PHOTOS:
				fragment = Fragment.instantiate(this,
						DummyFragment.class.getName());
				break;
			case CART:
				fragment = Fragment.instantiate(this,
						DummyFragment.class.getName());
				break;
			case PREFS:
				fragment = Fragment.instantiate(this,
						DummyFragment.class.getName());
				break;
			default:
				throw new java.lang.IllegalArgumentException("Unknown tab");
			}
			addFragment(fragment, backStack, ft);
		} else {
			// Show topmost fragment
			showFragment(backStack, ft);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		Log.d(TAG, "---- onTabUnselected");

		// Select proper stack
		Stack<String> backStack = backStacks.get(tab.getTag());

		// Get topmost fragment
		String tag = backStack.peek();
		Log.d(TAG, "tag:"+tag);
		Fragment fragment = getFragmentManager().findFragmentByTag(tag);

		// Detach it
		ft.detach(fragment);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		Log.d(TAG, "---- onTabReselected");

		// Select proper stack
		Stack<String> backStack = backStacks.get(tab.getTag());

		if (backStack.size() > 1){
			ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
		}

		// Clean the stack leaving only initial fragment
		while (backStack.size() > 1) {
			// Pop topmost fragment
			String tag = backStack.pop();
			Fragment fragment = getFragmentManager().findFragmentByTag(tag);

			// Remove it
			ft.remove(fragment);
		}
		showFragment(backStack, ft);
	}

	private void addFragment(Fragment fragment) {
		Log.d(TAG, "---- addFragment1");

		// Select proper stack
		Tab tab = getActionBar().getSelectedTab();
		Stack<String> backStack = backStacks.get(tab.getTag());

		FragmentTransaction ft = getFragmentManager().beginTransaction();

		// Animate transfer to new fragment
		ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

		// Get topmost fragment
		String tag = backStack.peek();
		Fragment top = getFragmentManager().findFragmentByTag(tag);
		ft.detach(top);

		// Add new fragment
		addFragment(fragment, backStack, ft);
		ft.commit();
	}

	private void addFragment(Fragment fragment, Stack<String> backStack, FragmentTransaction ft) {
		Log.d(TAG, "---- addFragment2");

		// Add fragment to back stack with unique tag
		String tag = UUID.randomUUID().toString();
		ft.add(android.R.id.content, fragment, tag);
		backStack.push(tag);
	}

	private void showFragment(Stack<String> backStack, FragmentTransaction ft) {
		Log.d(TAG, "---- showFragment");

		FragmentManager fm = getFragmentManager();
		Log.d(TAG, "BackStackEntryCount: " + fm.getBackStackEntryCount());

		// Peek topmost fragment from the stack
		String tag = backStack.peek();
		Fragment fragment = fm.findFragmentByTag(tag); // <== As Crash here!, NPE occurred!
		// and attach it
		ft.attach(fragment);
	}

	// The following code shows how to properly open new fragment. It assumes
	// that parent fragment calls its activity via interface. This approach
	// is described in Android development guidelines.
	@Override
	public void onItemSelected(String tabClassString, Bundle data) {
		Log.d(TAG, "---- onItemSelected tab:" + tabClassString);

		Fragment fragment = Fragment.instantiate(this, tabClassString);
		fragment.setArguments(data);
		addFragment(fragment);
	}

}

