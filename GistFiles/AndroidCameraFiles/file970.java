private Pubnub mPubnub;

@Override
protected void onCreate(Bundle savedInstanceState) {
    [ . . . ]
    mPubnub = new Pubnub("publish_key", "subscribe_key");
    try {
      mPubnub.subscribe("Channel Name Of Your Choice", subscribeCallback);
    } catch (PubnubException e) {
      Log.e(TAG, e.toString());
    }
}