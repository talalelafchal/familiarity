PNConfiguration pnc = new PNConfiguration().setSubscribeKey("subscribeKey").setPublishKey("publishKey");
PubNub pubnub = new PubNub(pnc);

pubnub.addListener(new SubscribeCallback() {
    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        System.out.println(message);
    }
});

pubnub.subscribe().channels(Arrays.asList("chatChannel")).execute();

Map message = new HashMap();
message.put("hello", "there");

pubnub.publish()
        .channel("chatChannel")
        .message(message)
        .async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                if (status.isError()) {
                    System.out.println(status);
                } else {
                    System.out.println("Published!");
                }
            }
        });