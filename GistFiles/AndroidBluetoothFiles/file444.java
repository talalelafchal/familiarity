protected void setAdvertiseData() {
  AdvertiseData.Builder mBuilder = new AdvertiseData.Builder()
  ByteBuffer mManufacturerData = ByteBuffer.allocate(24);
  byte[] uuid = getIdAsByte(UUID.fromString("0CF052C297CA407C84F8B62AAC4E9020"));
  mManufacturerData.put(0, (byte)0xBE); // Beacon Identifier
  mManufacturerData.put(1, (byte)0xAC); // Beacon Identifier
  for (int i=2; i<=17; i++) {
    mManufacturerData.put(i, uuid[i-2]); // adding the UUID
  }
  mManufacturerData.put(18, (byte)0x00); // first byte of Major
  mManufacturerData.put(19, (byte)0x09); // second byte of Major
  mManufacturerData.put(20, (byte)0x00); // first minor
  mManufacturerData.put(21, (byte)0x06); // second minor
  mManufacturerData.put(22, (byte)0xB5); // txPower
  mBuilder.addManufacturerData(224, mManufacturerData.array()); // using google's company ID
  mAdvertiseData = mBuilder.build();
}