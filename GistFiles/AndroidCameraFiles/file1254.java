// VideoChatActivity#hangup()
public void hangup(View view) {
    this.pnRTCClient.closeAllConnections();
    startActivity(new Intent(VideoChatActivity.this, MainActivity.class));
}