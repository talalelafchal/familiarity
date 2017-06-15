String channel = "YourCompany_"+major+"_"+minor;
try {
  pubnub.presence(channel, mPresenceCallback);
} catch (PubnubException e) {
  Log.d("PUBNUB",e.toString());
}