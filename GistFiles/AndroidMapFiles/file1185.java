private void generateNotification(Context context, Intent i) {
    PendingIntent intent;
    Helpshift hs = new Helpshift(context);
    if(i.getExtras().getString("origin").equals("helpshift")) {
        intent = hs.getPendingIntentOnPush(context, i);
    } 
    
    int icon = R.drawable.ic_launcher;
    Bundle extras = i.getExtras();
    String message = extras.getString("alert");
    long when = System.currentTimeMillis();

    Notification notification = new Notification(icon, message, when);
    String title = context.getString(R.string.app_name);
    notification.setLatestEventInfo(context, title, message, intent);
    notification.flags |= Notification.FLAG_AUTO_CANCEL;

    NotificationManager notificationManager = (NotificationManager)
        context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(0, notification);
}