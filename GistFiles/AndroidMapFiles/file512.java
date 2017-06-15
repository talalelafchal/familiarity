pubnub.addListener(new SubscribeCallback() {
    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        System.out.println(presence);
    }
});

pubnub.subscribe().channels(Arrays.asList("chatChannel")).withPresence().execute();