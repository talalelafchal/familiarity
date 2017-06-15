@Override
public void onMessage(PnPeer peer, Object message) {
    if (!(message instanceof JSONObject)) return; //Ignore if not JSONObject
    JSONObject jsonMsg = (JSONObject) message;
    try {
        String user = jsonMsg.getString("msg_user");
        String text = jsonMsg.getString("msg_text");
        final ChatMessage chatMsg = new ChatMessage(user, text);
        VideoChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VideoChatActivity.this,chatMsg.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    } catch (JSONException e){
        e.printStackTrace();
    }
}