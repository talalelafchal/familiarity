private void setOrientationBasedOnPreference() {
  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
  if (prefs.getBoolean(PreferencesActivity.KEY_DISABLE_AUTO_ORIENTATION, true)) {
    setRequestedOrientation(getCurrentOrientation());
  } else {
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
  }
}
