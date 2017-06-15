@Override
public void onNetPayBtConnected() {
    Log.e(LOG_CAT, "onNetPayBtConnected() ");
}

@Override
public void onNetPayBtDisconnected() {
    Log.e(LOG_CAT, "onNetPayBtDisconnected() ");
}

@Override
public void onNetPayBtNoDetected() {
    Log.e(LOG_CAT, "onNetPayBtNoDetected() ");
}

@Override
public void onNetPayBtError(QPOSService.Error error) {
    Log.e(LOG_CAT, "onNetPayBtError(): " + error);
}

@Override
public void onNetPayBtConnectedInfo(Hashtable<String, String> hashtable) {
    Log.e(LOG_CAT, "onNetPayBtConnectedInfo(): " + hashtable);
}