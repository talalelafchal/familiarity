package android.example.com.squawker.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class SquawkMessage extends FirebaseMessagingService {

    public static final String TAG_LOG = SquawkMessage.class.getSimpleName();
    public static final int SQUAWK_MESSAGE_PENDING_INTENT_ID = 56565;
    public static final int SQUAWK_NOTIFICATION_ID = 4545;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG_LOG, "Message received: "+ data);
        final String test = data.get("test");
        final String author = data.get("author");
        final String authorKey = data.get("authorKey");
        final String message = data.get("message");
        final Long date = Long.valueOf(data.get("date"));


        setupNotification(author, message);

        final AsyncTask<Void, Void, Void> taskInsertData = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                ContentValues cv = new ContentValues();
                cv.put(SquawkContract.COLUMN_AUTHOR, author);
                cv.put(SquawkContract.COLUMN_AUTHOR_KEY, authorKey);
                cv.put(SquawkContract.COLUMN_MESSAGE, message);
                cv.put(SquawkContract.COLUMN_DATE, date.toString());


                Uri uri = SquawkMessage.this.getContentResolver().insert(SquawkProvider
                        .SquawkMessages.CONTENT_URI, cv);
                Log.d(TAG_LOG, "Uri generated: " + uri);
                return null;
            }
        };

       taskInsertData.execute();

    }

    private void setupNotification(String author, String message) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_duck)
                .setLargeIcon(largeIcon(this, R.drawable.test))
                .setContentTitle("New messages from " + author)
                .setContentText(message.substring(0, 30))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(getPendingIntent())
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(SQUAWK_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static Bitmap largeIcon(Context context, int iconId) {
        // COMPLETED (5) Get a Resources object from the context.
        Resources res = context.getResources();
        // COMPLETED (6) Create and return a bitmap using BitmapFactory.decodeResource, passing in the
        // resources object and R.drawable.ic_local_drink_black_24px
        Bitmap largeIcon = BitmapFactory.decodeResource(res, iconId);
        return largeIcon;
    }

    @NonNull
    private PendingIntent getPendingIntent() {
        Intent intentToExecute = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, SQUAWK_MESSAGE_PENDING_INTENT_ID, intentToExecute,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
