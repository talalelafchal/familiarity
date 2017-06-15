String action = intent.getAction();
if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
    hs.setDeviceToken(PushManager.shared().getAPID());
}