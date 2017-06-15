public void getAdOfTheDay(int major, int minor) {
  String channel = "YourCompany_"+major+"_"+minor;
  try {
    pubnub.subscribe(channel, mPubNubCallback);
  } catch (PubnubException e) {
    System.out.println(e.toString());
  }
}