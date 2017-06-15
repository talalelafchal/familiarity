@Override
public void onReceive(Context context, Intent intent) {
    Helpshift hs = new Helpshift(context);
    final String regId = GCMRegistrar.getRegistrationId(context);
    hs.setDeviceToken(regId);
}