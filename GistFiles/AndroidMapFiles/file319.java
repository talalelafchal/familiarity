pubnub.addListener(new SubscribeCallback() {
    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        System.out.println(message);
    }
});

pubnub.addChannelsToChannelGroup()
        .channels(Arrays.asList("chatChannel1", "chatChannel2"))
        .channelGroup("channelGroup1")
        .async(new PNCallback<PNChannelGroupsAddChannelResult>() {
            @Override
            public void onResponse(PNChannelGroupsAddChannelResult result, PNStatus status) {
                if (status.isError()) {
                    System.out.println("adding channels to channel group failed");
                } else {
                    pubnub.subscribe().channelGroups(Arrays.asList("channelGroup1")).execute();
                }
            }
        });