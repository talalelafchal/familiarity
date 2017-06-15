package com.example.android;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class MyInfoWindowAdapter implements InfoWindowAdapter {

	private View myContentsView;
	HashMap<String,JSONObject> mHashmap;


	public MyInfoWindowAdapter(Activity mActivity, HashMap<String,JSONObject> hashmap) {
		myContentsView = mActivity.getLayoutInflater().inflate(
				R.layout.custom_info_contents, null);
		
		mHashmap = hashmap;
	}

	@Override
	public View getInfoContents(final Marker marker) {
		
		TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
		tvTitle.setText(marker.getTitle());
		TextView tvSnippet = ((TextView) myContentsView
				.findViewById(R.id.snippet));
		tvSnippet.setText(marker.getSnippet());
		
		
		return myContentsView;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}