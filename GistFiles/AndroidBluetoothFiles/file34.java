private void setScanSettings() {
  ScanSettings.Builder mBuilder = new ScanSettings.Builder();
  mBuilder.setReportDelay(0);
  mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
  mScanSettings = mBuilder.build();
}