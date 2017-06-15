package pl.tajchert.cat.sendmeow;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

import pl.tajchert.cat.meow.Tools;


public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerListenerService";
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            Log.d(TAG, "Update, path: " +path);
            if (Tools.WEAR_PATH.equals(path)) {
                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                String meowText = item.getDataMap().getString(Tools.WEAR_KEY_MEOW_TEXT);
                Log.d(TAG, "got: " + meowText);
	            showNotification();
//	            openApp();
                if(meowText != null) {
                    meowText = meowText.replaceAll("\\d","");//Hide numbers
                    if(!meowText.equals("")){
                        //Pass by using sharedPreferences to Activity
                        getBaseContext().getSharedPreferences(Tools.PREFS, MODE_PRIVATE).edit().putString(Tools.PREFS_KEY_MEOW_TEXT, meowText).commit();
                        getBaseContext().sendBroadcast(new Intent(Tools.DATA_CHANGED_ACTION));
                    }
                }
            }
        }
    }

    @Override
    public void onPeerConnected(Node peer) {}

    @Override
    public void onPeerDisconnected(Node peer){}

	public void showNotification() {
		Log.d(TAG, "Show watch notification only!");
		Intent viewIntent = new Intent(this, MyActivity.class);
		PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle("Kittenz!")
						.setContentText("Cute kittenz are cute!")
						.setContentIntent(viewPendingIntent);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
		notificationManager.notify(1999, notificationBuilder.build());
	}

	public void openApp() {
		startActivity(new Intent(this, MyActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

}
