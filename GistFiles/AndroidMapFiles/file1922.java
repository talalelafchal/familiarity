public Notification buildNotification(String alert, Map<String, String> extras) {
    if(!hs.isForeground()) {
        return super.buildNotification(alert, extras);
    } else {
        return null;
    }
}