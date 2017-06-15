Callback subscribeCallback = new Callback() {

  @Override
  public void successCallback(String channel, Object message) {
    Log.d(PUBNUB_TAG, "Message Received: " + message.toString());
  }
};