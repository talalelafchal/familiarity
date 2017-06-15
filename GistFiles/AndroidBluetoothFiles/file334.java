public Observable<Boolean> enablePushNotifications(boolean enable) {
  return Observable.fromCallable(() -> sharedPrefs
    .edit()
    .putBoolean(KEY_PUSH_NOTIFICATIONS_PREFS, enable)
    .commit());
}