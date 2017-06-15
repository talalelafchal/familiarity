package com.ozateck.oz_wearsyncdata;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.util.Log;
import android.support.wearable.view.WatchViewStub;

import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Asset;


public class MainActivity extends Activity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		DataApi.DataListener{

	private static final String TAG = "MainActivity";

	private static final String COUNT_PATH = "/count/path";
	private static final String COUNT_KEY = "COUNT_KEY";

	private static final String IMAGE_PATH = "/image/path";
	private static final String IMAGE_KEY = "IMAGE_KEY";

	private GoogleApiClient mGoogleApiClient;

	private TextView textView;
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle icicle){
		super.onCreate(icicle);
		setContentView(R.layout.activity_main);

		// GoogleApiClient
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wearable.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
		mGoogleApiClient.connect();

		// UI
		WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub){
				textView = (TextView)stub.findViewById(R.id.text_count);
				imageView = (ImageView)stub.findViewById(R.id.image_sample);
				if(textView == null) Log.d(TAG, "textView:null");
				if(imageView == null) Log.d(TAG, "imageView:null");
			}
		});
	}

	@Override
	protected void onResume() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Wearable.DataApi.removeListener(mGoogleApiClient, this);
		mGoogleApiClient.disconnect();
	}

	//==========
	// GoogleApiClient
	@Override
	public void onConnected(Bundle bundle){
		Log.d(TAG, "onConnected");
		Wearable.DataApi.addListener(mGoogleApiClient, this);
	}

	@Override
	public void onConnectionSuspended(int cause){
		Log.d(TAG, "onConnectionSuspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result){
		Log.d(TAG, "onConnectionFailed");
	}

	//==========
	// DataListener
	@Override
	public void onDataChanged(DataEventBuffer dataEvents){
		Log.d(TAG, "onDataChanged");

		for(DataEvent event : dataEvents){

			DataItem dataItem = event.getDataItem();
			Log.d(TAG, "Path:" + dataItem.getUri().getPath());

			if(COUNT_PATH.equals(dataItem.getUri().getPath())){
				Log.d(TAG, "COUNT_PATH:" + dataItem.getUri().getPath());
				DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
				int count = dataMap.getInt(COUNT_KEY);
				updateTextView(this, "Count:" + count);
				break;
			}

			if(IMAGE_PATH.equals(dataItem.getUri().getPath())){
				Log.d(TAG, "IMAGE_PATH:" + dataItem.getUri().getPath());
				DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
				Asset asset = dataMapItem.getDataMap().getAsset(IMAGE_KEY);
				Bitmap bitmap = ImageDecoder.decAst2Bmp(mGoogleApiClient, asset);
				updateImageView(this, bitmap);
				break;
			}
		}
	}

	private void updateTextView(final Activity activity, final String str){

		this.runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(activity, "updateTextView", Toast.LENGTH_SHORT).show();
				textView.setText(str);
			}
		});
	}

	private void updateImageView(final Activity activity, final Bitmap bitmap){

		this.runOnUiThread(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(activity, "updateImageView", Toast.LENGTH_SHORT).show();
				imageView.setImageBitmap(bitmap);
			}
		});
	}
}