protected ScanCallback mScanCallback = new ScanCallback() {
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        ScanRecord mScanRecord = result.getScanRecord();
        int[] manufacturerData = mScanRecord.getManufacturerSpecificData(224);
        int mRssi = result.getRssi();
    }
}