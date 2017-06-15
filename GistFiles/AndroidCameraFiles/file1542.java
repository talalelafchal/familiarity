package com.ivanovsuper.notificationaction;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String BROADCAST_PLAYBACK_PLAY = "com.ivanovsuper.notificationaction.PLAY";
    private static final String BROADCAST_PLAYBACK_PAUSE = "com.ivanovsuper.notificationaction.PAUSE";
    private static final int NOTIFICATION_ID = 147;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && intent.getAction().equals(BROADCAST_PLAYBACK_PLAY)){
                Toast.makeText(context, intent.getAction(),Toast.LENGTH_SHORT).show();
            } else if(intent!=null && intent.getAction().equals(BROADCAST_PLAYBACK_PAUSE)){
                Toast.makeText(context, intent.getAction(),Toast.LENGTH_SHORT).show();
            }
        }
    };
    IntentFilter intentFilter = new IntentFilter();
    {
        intentFilter.addAction(BROADCAST_PLAYBACK_PLAY);
        intentFilter.addAction(BROADCAST_PLAYBACK_PAUSE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_menu_share)
                .setTicker("текст")
                .setContentTitle("текст")
                .setContentText("Нажмите чтобы вернуться в приложение")
                .setOngoing(false)
                .setAutoCancel(false);
        builder.addAction(R.drawable.ic_menu_camera, "Pause", makePendingIntent(BROADCAST_PLAYBACK_PLAY) )
                .addAction(R.drawable.ic_menu_gallery, "Play", makePendingIntent(BROADCAST_PLAYBACK_PAUSE));
        NotificationManagerCompat manager =  NotificationManagerCompat.from(this);
        manager.notify(0, builder.build());
    }

    private PendingIntent makePendingIntent(String action) {
        return PendingIntent.getBroadcast(this, 0, new Intent(action), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
