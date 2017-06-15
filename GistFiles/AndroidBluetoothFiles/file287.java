protected Callback mPubNubCallback = new Callback() {
  @Override
  public void successCallback(String channel, Object message) {
    System.out.println(message.toString());
  }
};