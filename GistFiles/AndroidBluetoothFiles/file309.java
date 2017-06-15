protected Callback mPresenceCallback = new Callback() {
  @Override
  public void successCallback(String channel, Object message) {
    pubnub.publish(channel, "Come in now to get $2 off your next drink!", new Callback() {});
  }
};
