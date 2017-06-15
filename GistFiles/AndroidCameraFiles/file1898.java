JSONObject hangupMsg = PnPeerConnectionClient.generateHangupPacket("myUsername");
this.mPubNub.publish("userCalling",hangupMsg, new Callback() {
    @Override
    public void successCallback(String channel, Object message) {
        Intent intent = new Intent(IncomingCallActivity.this, MainActivity.class);
        startActivity(intent);
    }
});