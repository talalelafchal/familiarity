@Override
public void onScanResult(int callbackType, ScanResult result) {
  [...]
  if ((mBeaconScanRecord.getDistance() == "Immediate") && !isSubscribed()) {
    getAdOfTheDay(mIBeaconScanRecord.getMajor(), mIBeaconScanRecord.getMinor());
  }
}