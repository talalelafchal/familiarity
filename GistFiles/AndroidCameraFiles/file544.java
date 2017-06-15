package com.PhysicsPhantom.Launcher2;  	// ----- Code by Nicholas Andersson, Max Dahlin, Erik Lindmaa  feel free to use  email: nicken333@gmail.com----- //


import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class AppGrid extends Activity {

	PackageManager myPackageManager;

	public class MyBaseAdapter extends BaseAdapter {

		private Context myContext;
		private List<ResolveInfo> MyAppList;

		MyBaseAdapter(Context c, List<ResolveInfo> l) {
			myContext = c;
			MyAppList = l;
		}

		public int getCount() {
			return MyAppList.size();
		}

		public Object getItem(int position) {
			return MyAppList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView imageView;
			if (convertView == null) {
				// if it's not recycled, initialize some attributes
				imageView = new ImageView(myContext);
				imageView.setLayoutParams(new GridView.LayoutParams(130, 130));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(24, 40, 24, 40);
			} else {
				imageView = (ImageView) convertView;
			}

			ResolveInfo resolveInfo = MyAppList.get(position);
			imageView.setImageDrawable(resolveInfo.loadIcon(myPackageManager));

			return imageView;

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appgrid);

		myPackageManager = getPackageManager();

		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> intentList = getPackageManager()
				.queryIntentActivities(intent, 0);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new MyBaseAdapter(this, intentList));

		gridview.setOnItemClickListener(myOnItemClickListener);
	}

	OnItemClickListener myOnItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ResolveInfo cleckedResolveInfo = (ResolveInfo) parent
					.getItemAtPosition(position);
			ActivityInfo clickedActivityInfo = cleckedResolveInfo.activityInfo;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setClassName(
					clickedActivityInfo.applicationInfo.packageName,
					clickedActivityInfo.name);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			startActivity(intent);

		}

	};

}