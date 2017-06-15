package com.pariscope.ui.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.pariscope.R;
import com.pariscope.base.BaseActivity;
import com.pariscope.bean.EventItem;
import com.pariscope.bean.Place;
import com.pariscope.bean.Transport;
import com.pariscope.ui.fragments.FragmentPlaceDetail;
import com.pariscope.utils.ApiCall;
import com.pariscope.utils.Constants;

public class DetailPlace extends BaseActivity {

  private Place place;
	private FragmentTabHost mTabHost;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_place);


		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		map = fragment.getMap();

   		Bundle b = new Bundle();
		b.putParcelable("place", place);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(),
				android.R.id.tabcontent);
		mTabHost.addTab(
				mTabHost.newTabSpec("simple").setIndicator("Infos salle"),
				FragmentPlaceDetail.class, b);
		mTabHost.addTab(
				mTabHost.newTabSpec("ongoing").setIndicator("A l'affiche"),
				FragmentPlaceDetail.class, b);
		mTabHost.addTab(mTabHost.newTabSpec("toCome").setIndicator("Bientôt"),
				FragmentPlaceDetail.class, b);
  }
}
