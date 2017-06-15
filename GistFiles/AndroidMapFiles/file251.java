package io.twg.wearapp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

/**
 * Receive Messages from Device
 */

public class DataListenerService extends WearableListenerService {

    private static final String TAG = DataListenerService.class.getSimpleName();
    public final static String DATA_PATH_TO_WEARABLE = "/wearable-message";
    public final static String DATA = "data";

    private WearableApplication mApplication;
    private GoogleApiClient mClient;
    private static MessageListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = (WearableApplication) getApplicationContext();
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (!mClient.isConnected() || !mClient.isConnecting()) {
            ConnectionResult connectionResult = mClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "DataLayerListenerService failed to connect to GoogleApiClient, "
                        + "error code: " + connectionResult.getErrorCode());
                return;
            }
        }

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(DATA_PATH_TO_WEARABLE) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    if (mApplication.isMainViewLaunched()) {
                        if (mListener != null) {
                            mListener.receiveMessage(dataMap.getString(DATA));
                        }
                    }
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {

            }
        }
    }

    public interface MessageListener {
        void receiveMessage(String data);
    }

    public static void setListener(@NonNull MessageListener listener) {
        mListener = listener;
    }

}
