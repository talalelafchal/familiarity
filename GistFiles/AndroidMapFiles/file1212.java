package put.your.packageName.here;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.example.anik.agent.AllComplaintActivity;
import com.example.anik.agent.BillListActivity;
import com.example.anik.agent.OrderListActivity;
import com.example.anik.agent.R;
import com.example.anik.agent.ShowProduct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anik on 07-Aug-15, 007.
 */
public class AppNotification {
    public static int NOTIFICATION_TYPE_A = 1;
    public static int NOTIFICATION_TYPE_B = 2;
    public static int NOTIFICATION_TYPE_C = 3;
    public static int NOTIFICATION_TYPE_D = 4;
    public static int NOTIFICATION_TYPE_E = 5;
    public static int NOTIFICATION_TYPE_F = 6;
    public static int NOTIFICATION_TYPE_G = 7;
    private static AppNotification instance = null;

    private Context context;
    private int notificationType = 0;
    private String title = "";
    private String body = "";
    private Map<String, String> extraInformation = new HashMap<>();
    private NotificationManager notificationManager;

    private AppNotification(Context context, int type) {
        this.context = context;
        this.notificationType = type;
    }

    public static AppNotification build(Context context, int type) {
        if (type != NOTIFICATION_TYPE_A
                && type != NOTIFICATION_TYPE_C
                && type != NOTIFICATION_TYPE_D
                && type != NOTIFICATION_TYPE_E
                && type != NOTIFICATION_TYPE_F
                && type != NOTIFICATION_TYPE_G
                ) {
            return null;
        }

        if (instance == null) {
            instance = new AppNotification(context, type);
        }
        return instance;
    }

    public static AppNotification setTitle(String title) {
        instance.title = title;
        return instance;
    }

    public static AppNotification setBody(String body) {
        instance.body = body;
        return instance;
    }

    public static AppNotification setExtraInformation(Map<String, String> information) {
        instance.extraInformation = information;
        return instance;
    }

    public static void send() {
        instance.notificationManager = (NotificationManager) instance.context.getSystemService(instance.context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(instance.context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(instance.title)
                .setContentText(instance.body)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        Intent nextIntent = null;
        if (instance.getNotificationType() == NOTIFICATION_TYPE_A) {
            nextIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        } else if (instance.getNotificationType() == NOTIFICATION_TYPE_C) {
            nextIntent = new Intent(instance.context, ShowProduct.class);
            if (!instance.extraInformation.isEmpty()) {
                for (Map.Entry<String, String> row : instance.extraInformation.entrySet()) {
                    nextIntent.putExtra(row.getKey(), row.getValue());
                }
            }

        } else if (instance.getNotificationType() == NOTIFICATION_TYPE_D) {
            nextIntent = new Intent(instance.context, OrderListActivity.class);

        } else if (instance.getNotificationType() == NOTIFICATION_TYPE_E) {
            nextIntent = new Intent(instance.context, BillListActivity.class);

        } else if (instance.getNotificationType() == NOTIFICATION_TYPE_F) {
            nextIntent = new Intent(instance.context, BillListActivity.class);

        } else if (instance.getNotificationType() == NOTIFICATION_TYPE_G) {
            nextIntent = new Intent(instance.context, AllComplaintActivity.class);

        } else if (instance.getNotificationType() == NOTIFICATION_TYPE_B) {
            // todo here
            // nextIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        }
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        instance.context,
                        101,
                        nextIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(pendingIntent);
        instance.notificationManager.notify(1123, mBuilder.build());
        instance = null;
    }

    public int getNotificationType() {
        return notificationType;
    }
}
