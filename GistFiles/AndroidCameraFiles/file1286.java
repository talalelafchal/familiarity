// automated restoration usually does its job... i.e. don't need to implement
@Override
protected void onStop() {
  super.onStop(); //again, call this first. always.
  
  ContentValues values = new ContentValues();
  values.put(SOME_TITLE, getTitle());
  values.put(SOME_VALUE, getValue());
  
  getContentResolver().update(
    mUri,
    values,
    null,
    null
  )
}

// use onStart() to counter onStop(), rather than onRestart().... in most cases
@Override
protected void onStart() {
  super.onStart();
  
  if (!gpsEnabled) {
    enableGPS();
  }
}