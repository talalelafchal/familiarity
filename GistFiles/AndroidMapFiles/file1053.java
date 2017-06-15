package com.getpebble.example;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PebbleIntentsActivity extends Activity {

    private static String TAG = "pebble-intents-example";
    private List<BroadcastReceiver> registeredReceivers = new ArrayList<BroadcastReceiver>();


    private static int songIndex = 0;
    private static final String[][] NOW_PLAYING_DATA = {
            {"Prince", "Purple Rain", "When Doves Cry"},
            {"Primus", "Pork Soda", "Hamburger Train"},
            {"Carly Rae Jepsen", "Kiss", "Call Me Maybe"},
            {"Kenny Loggins", "Top Gun", "Danger Zone"}
    };

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     *         If the activity is being re-initialized after previously being shut down then this Bundle contains the
     *         data it most recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterPebbleBroadcastReceivers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerPebbleBroadcastReceivers();
    }

    private void registerPebbleBroadcastReceivers() {
        IntentFilter connectedBroadcastFilter = new IntentFilter("com.getpebble.action.PEBBLE_CONNECTED");
        BroadcastReceiver connectedBroadcastReceiver = new ConnectedBroadcastReceiver();
        registerReceiver(connectedBroadcastReceiver, connectedBroadcastFilter);
        registeredReceivers.add(connectedBroadcastReceiver);

        IntentFilter disconnectedBroadcastFilter = new IntentFilter("com.getpebble.action.PEBBLE_DISCONNECTED");
        BroadcastReceiver disconnectedBroadcastReceiver = new DisconnectedBroadcastReceiver();
        registerReceiver(disconnectedBroadcastReceiver, disconnectedBroadcastFilter);
        registeredReceivers.add(disconnectedBroadcastReceiver);
    }

    private void unregisterPebbleBroadcastReceivers() {
        while (!registeredReceivers.isEmpty()) {
            unregisterReceiver(registeredReceivers.remove(0));
        }
    }

    public void sendAlertToPebble(View view) {
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("title", "Test Message");
        data.put("body", "Whoever said nothing was impossible never tried to slam a revolving door.");
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "MyAndroidApp");
        i.putExtra("notificationData", notificationData);

        Log.d(TAG, "About to send a modal alert to Pebble: " + notificationData);
        sendBroadcast(i);
    }

    public void sendMusicUpdateToPebble(View view) {
        songIndex = ++songIndex % (NOW_PLAYING_DATA.length);
        String[] trackInfo = NOW_PLAYING_DATA[songIndex];


        final Intent i = new Intent("com.getpebble.action.NOW_PLAYING");
        i.putExtra("artist", trackInfo[0]);
        i.putExtra("album", trackInfo[1]);
        i.putExtra("track", trackInfo[2]);

        sendBroadcast(i);
    }

    private static class ConnectedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String pebbleAddress = intent.getStringExtra("address");
            Log.i(TAG, String.format("Pebble (%s) connected", pebbleAddress));
        }
    }

    private static class DisconnectedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String pebbleAddress = intent.getStringExtra("address");
            Log.i(TAG, String.format("Pebble (%s) disconnected", pebbleAddress));
        }
    }

}
