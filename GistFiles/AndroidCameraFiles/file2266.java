package com.ozateck.oz_tripple;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.content.res.Configuration;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import android.widget.AdapterView;
import android.widget.ListView;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.DrawerLayout;

import com.ozateck.oz_tripple.drawer.DrawerListAdapter;
import com.ozateck.oz_tripple.drawer.DrawerListData;
import com.ozateck.oz_tripple.fragments.MushroomFragment;
import com.ozateck.oz_tripple.fragments.MyMapFragment;

public class MainActivity extends ActionBarActivity{

	private static final String TAG = "MainActivity";

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;

	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);

		//==========
		// Toolbar
		Toolbar tBar = (Toolbar)this.findViewById(R.id.my_toolbar);
		tBar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
		tBar.setNavigationContentDescription("NavDescription");
		//tBar.setLogo(R.drawable.ic_launcher);
		//tBar.setLogoDescription("LogoDescription");
		this.setSupportActionBar(tBar);

		//==========
		// Fragment
		setFragment(0);

		//==========
		// Drawer
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this,
				mDrawerLayout, tBar,R.string.drawer_open, R.string.drawer_close){

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset){
				super.onDrawerSlide(drawerView, slideOffset);
				//Log.d(TAG, "onDrawerSlider_slideOffset:" + slideOffset);
			}

			@Override
			public void onDrawerOpened(View drawerView){
				super.onDrawerOpened(drawerView);
				Log.d(TAG, "onDrawerOpened");
			}

			@Override
			public void onDrawerClosed(View drawerView){
				super.onDrawerClosed(drawerView);
				Log.d(TAG, "onDrawerClosed");
			}

			@Override
			public void onDrawerStateChanged(int newState){
				// 表示済み、閉じ済みの状態：0
				// ドラッグ中状態：1
				// ドラッグを離したあとのアニメーション中：2
				super.onDrawerStateChanged(newState);
				Log.d(TAG, "onDrawerStateChanged_drawerState:" + newState);
			}
		};
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		//==========
		// ListView
		ArrayList dDatas = new ArrayList<DrawerListData>();
		DrawerListAdapter adapter = new DrawerListAdapter(this, dDatas);
		adapter.add(new DrawerListData(R.drawable.ic_android_black_24dp, "Information", 0));
		adapter.add(new DrawerListData(R.drawable.ic_pin_drop_black_24dp, "Map", 1));
		adapter.add(new DrawerListData(R.drawable.ic_collections_black_24dp, "Gallery", 2));
		adapter.add(new DrawerListData(R.drawable.ic_photo_camera_black_24dp, "Camera", 3));
		adapter.add(new DrawerListData(R.drawable.ic_photo_camera_black_24dp, "-", DrawerListAdapter.DIVIDING_INDEX));
		adapter.add(new DrawerListData(R.drawable.ic_settings_black_24dp, "Setting", 4));
		adapter.add(new DrawerListData(R.drawable.ic_live_help_black_24dp, "Help", 5));
		mDrawerList = (ListView)this.findViewById(R.id.drawer_list);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(
					AdapterView<?> parent, View view, int position, long id){
				onItemClicked(position);
			}
		});
	}

	//==========
	// Toolbar
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		if(id == R.id.action_settings){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//==========
	// Fragment
	private void setFragment(int number){

		if(number == 1){

			// MyMapFragment
			Fragment fragment = MyMapFragment.createFragment();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.drawer_body, fragment)
					.commit();
		}else{

			// MushroomFragment
			Fragment fragment = MushroomFragment.createFragment();
			Bundle args = new Bundle();
			args.putInt(MushroomFragment.MUSHROOM_NUMBER, number);
			fragment.setArguments(args);

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.drawer_body, fragment)
					.commit();
		}
	}

	//==========
	// Drawer
	@Override
	protected void onPostCreate(Bundle icicle) {
		super.onPostCreate(icicle);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	//==========
	// ListView
	private void onItemClicked(int position){
		Log.d(TAG, "onItemClicked:" + position);

		DrawerListData dData = (DrawerListData)mDrawerList.getItemAtPosition(position);

		// Title
		this.setTitle(dData.title);

		// Fragment
		setFragment(dData.index);

		// Drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawers();
	}
}
