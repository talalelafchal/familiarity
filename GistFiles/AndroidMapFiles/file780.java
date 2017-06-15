@Override
protected void onRegistered(Context context, String registrationId) {
    Intent intent = new Intent();
    intent.setAction("YOUR_ACTION_NAME");
    sendBroadcast(intent);
}