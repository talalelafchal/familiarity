import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by vassilis on 5/8/16.
 */
public class Notification {
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    private Context context;
    int id = 1;

    // mock coordinates
    private Double myLatitude = 40.2999489;
    private Double myLongitude = 21.8031888;
    // coordinates for marker
    private Double latitude;
    private Double longitude;

    public Notification(Context context) {
        this.context = context;
    }

    public Notification(Context context, double latitude, double longitude) {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void showNotification() {

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(context.getResources().getString(R.string.notification_content_text))
                .setSmallIcon(R.mipmap.ic_launcher);

        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=(%s)@%f,%f", myLatitude, myLongitude, context.getString(R.string.marker_label), latitude, longitude);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.no_intent_app), Toast.LENGTH_SHORT).show();
        }
        // updates the notification
        mBuilder.setAutoCancel(true);

        notificationManager.notify(id, mBuilder.build());
    }
}