package at.maui.bunting.data;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by maui on 04.07.2014.
 */
public class WearDataListenerService extends WearableListenerService {

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if(!mGoogleApiClient.isConnected())
            mGoogleApiClient.blockingConnect();

            final Uri url = Uri.parse(messageEvent.getPath());
            
        if(messageEvent.getPath().equals("/wear/wear_error")) {

            DataMap map = DataMap.fromByteArray(messageEvent.getData());

            ByteArrayInputStream bis = new ByteArrayInputStream(map.getByteArray("exception"));
            try {
                ObjectInputStream ois = new ObjectInputStream(bis);
                Throwable ex = (Throwable) ois.readObject();

                Crashlytics.setBool("wear_exception", true);
                Crashlytics.setString("board", map.getString("board"));
                Crashlytics.setString("fingerprint", map.getString("fingerprint"));
                Crashlytics.setString("model", map.getString("model"));
                Crashlytics.setString("manufacturer", map.getString("manufacturer"));
                Crashlytics.setString("product", map.getString("product"));
                Crashlytics.logException(ex);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else if(messageEvent.getPath().equals("/some-other-path") {
            // Handle all the other events synced using the Messaging API
        }
    }
}
