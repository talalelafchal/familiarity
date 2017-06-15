public class NotificationListener extends NotificationListenerService {
    Map<String, String> notifications = new ConcurrentHashMap<>()
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(NOTIFICATION_LISTENER_TAG, "started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(NOTIFICATION_LISTENER_TAG, "destroyed");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification added) {
        Log.d(NOTIFICATION_LISTENER_TAG, "New notification from" + added.getPackageName());
        if (added.isClearable() && added.getNotification().priority >= android.app.Notification.PRIORITY_LOW) {
            notifications.put(getUniqueKey(added), getText(added))
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification removed) {
        Log.d(NOTIFICATION_LISTENER_TAG, "Notification removed " + removed.getNotification().tickerText);
        notifications.remove(getUniqueKey(removed));
    }

    private String getUniqueKey(StatusBarNotification notification) {
        return notification.getPackageName().concat(":").concat(String.valueOf(notification.getId()));
    }

    private String getText(StatusBarNotification notification) {
       return notification.getNotification().extras.getString(Notification.EXTRA_TEXT)
    }
}