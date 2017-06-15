// VideoChatActivity#onCreate()
// First attach the RTC Listener so that callback events will be triggered
this.pnRTCClient.attachRTCListener(new MyRTCListener());
this.pnRTCClient.attachLocalMediaStream(mediaStream);

// Listen on a channel. This is your "phone number," also set the max chat users.
this.pnRTCClient.listenOn(this.username);
this.pnRTCClient.setMaxConnections(1);

// If Constants.CALL_USER is in the intent extras, auto call them.
if (extras.containsKey(Constants.CALL_USER)) {
  String callUser = extras.getString(Constants.CALL_USER, "");
  connectToUser(callUser);
}