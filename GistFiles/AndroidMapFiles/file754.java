@Override
protected void onMessage(Context context, Intent intent) {
    Helpshift hs = new Helpshift(context);
    
    if(!hs.isForeground()) {
        generateNotification(context, intent);
    }
}