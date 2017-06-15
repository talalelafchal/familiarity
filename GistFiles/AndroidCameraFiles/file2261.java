@Override
public void onPeerConnectionClosed(PnPeer peer) {
    Intent intent = new Intent(VideoChatActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
}