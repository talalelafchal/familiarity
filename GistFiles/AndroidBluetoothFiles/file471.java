private void setScanFilter() {
  ScanFilter.Builder mBuilder = new ScanFilter.Builder();
  ByteBuffer mManufacturerData = ByteBuffer.allocate(23);
  ByteBuffer mManufacturerDataMask = ByteBuffer.allocate(24);
  byte[] uuid = getIdAsByte(UUID.fromString("0CF052C297CA407C84F8B62AAC4E9020");
  mManufacturerData.put(0, (byte)0xBE);
  mManufacturerData.put(1, (byte)0xAC);
  for (int i=2; i<=17; i++) {
    mManufacturerData.put(i, uuid[i-2]);
  }
  for (int i=0; i<=17; i++) {
    mManufacturerDataMask.put((byte)0x01);
  }
  mBuilder.setManufacturerData(224, mManufacturerData.array(), mManufacturerDataMask.array());
  mScanFilter = mBuilder.build();
}