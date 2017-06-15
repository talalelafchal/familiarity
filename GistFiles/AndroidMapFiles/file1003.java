String action = intent.getAction();
if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {
    if(intent.getExtras().getString("origin").equals("helpshift")) {
        hs.showSupportOnPush(UAirship.shared().getApplicationContext(),
                             intent);
    }
}