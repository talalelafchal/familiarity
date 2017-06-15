private Subscription subscription;

@Override protected void onResume() {
  super.onResume();

  // optionally, we can request Bluetooth Access
  reactiveBeacons.requestBluetoothAccessIfDisabled(this);

  subscription = reactiveBeacons.observe()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<Beacon>() {
      @Override public void call(Beacon beacon) {
        // do something with beacon
      }
    });
}    