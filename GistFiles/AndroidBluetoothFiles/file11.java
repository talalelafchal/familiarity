protected void setAdvertiseSettings() {
  AdvertiseSettings.Builder mBuilder = new AdvertiseSettings.Builder();
  mBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
  mBuilder.setConnectable(false);
  mBuilder.setTimeout(0);
  mBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
  mAdvertiseSettings = mBuilder.build();
}