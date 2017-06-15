private void sendMsg(String msgText){
    JSONObject msgJson = new JSONObject();
    try {
        msgJson.put("msg_user", username);
        msgJson.put("msg_text", msgText);
        this.pnRTCClient.transmitAll(msgJson);
    } catch (JSONException e){
        e.printStackTrace();
    }
}