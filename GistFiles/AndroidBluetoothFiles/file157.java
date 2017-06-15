package andrej.jelic.attendance;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "Start receiver: ";
    private static final int MY_NOTIFICATION_ID_STARTED = 1;
    private static final int MY_NOTIFICATION_ID_FINISHED = 2;
    private static final int REQUEST_CODE_START = 1;
    private static final int REQUEST_CODE_FINISHED = 2;
    private static final int REQUEST_CANCEL_ALARM = 3;

    private Intent mNotificationIntent;
    private PendingIntent mNotificationPendingIntent;
    private Notification.Builder mNotificationBuilder;

    private final CharSequence tickerTextStarted = "New attendance session just started!";
    private final CharSequence contentTitle = "Attendance";
    private final CharSequence contentText = "Students present";

    private final CharSequence tickerTextFinished = "Attendance session finished";
    private final long[] mVibratePattern = {0, 200, 200, 300};
    private NotificationManager mNotificationManager;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int requestCode = intent.getIntExtra("id", -1);

        switch (requestCode) {

            case REQUEST_CODE_START:

                mNotificationIntent = new Intent(context, Started_at_time.class);
                mNotificationPendingIntent = PendingIntent.getActivity(context, 0,
                        mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

                mNotificationBuilder = new Notification.Builder(context)
                        .setTicker(tickerTextStarted)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.logo_etfos)
                        .setContentIntent(mNotificationPendingIntent)
                        .setVibrate(mVibratePattern);

                mNotificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT < 16) mNotificationBuilder.getNotification();
                else
                    mNotificationManager.notify(MY_NOTIFICATION_ID_STARTED, mNotificationBuilder.build());

                break;

            case REQUEST_CODE_FINISHED:

                mNotificationIntent = new Intent(context, Finished.class);
                mNotificationPendingIntent = PendingIntent.getActivity(context, 0,
                        mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

                mNotificationBuilder = new Notification.Builder(context)
                        .setTicker(tickerTextFinished)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.logo_etfos)
                        .setContentIntent(mNotificationPendingIntent)
                        .setVibrate(mVibratePattern);

                mNotificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT < 16) mNotificationBuilder.getNotification();
                else
                    mNotificationManager.notify(MY_NOTIFICATION_ID_FINISHED, mNotificationBuilder.build());

                break;


        }
    }
}
