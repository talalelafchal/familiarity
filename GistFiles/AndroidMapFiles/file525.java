package com.ozateck.oz_wearsyncdata;

import android.util.Log;

import com.google.android.gms.wearable.WearableListenerService;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;

public class MyWearableListenerService extends WearableListenerService{

	private static final String TAG = "MyWearableListenerService";

	private static final String MESSAGE_PATH = "/message/path";

	private static final String COUNT_PATH = "/count/path";
	private static final String COUNT_KEY = "COUNT_KEY";

	@Override
	public void onMessageReceived(MessageEvent messageEvent){
		Log.d(TAG, "onMessageReceived");

		if(messageEvent.getPath().equals(MESSAGE_PATH)){
			Log.d(TAG, "Do something");
		}
	}

	@Override
	public void onDataChanged(DataEventBuffer dataEventBuffer){
		Log.d(TAG, "onDataChanged");

		for (DataEvent event : dataEventBuffer) {
			DataItem dataItem = event.getDataItem();
			if (COUNT_PATH.equals(dataItem.getUri().getPath())) {
				DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
				int count = dataMap.getInt(COUNT_KEY);
				Log.d(TAG, "count:" + count);
				break;
			}
		}

	}
}
