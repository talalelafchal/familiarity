public void makeCall(View view){
    String callNum = mCallNumET.getText().toString();
    if (callNum.isEmpty() || callNum.equals(this.username)) {
        Toast.makeText(this, "Enter a valid number.", Toast.LENGTH_SHORT).show();   
    }
    dispatchCall(callNum);
}

public void dispatchCall(final String callNum) {
    final String callNumStdBy = callNum + Constants.STDBY_SUFFIX;
    JSONObject jsonCall = new JSONObject();
    try {
        jsonCall.put(Constants.JSON_CALL_USER, this.username);
        mPubNub.publish(callNumStdBy, jsonCall, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.d("MA-dCall", "SUCCESS: " + message.toString());
                Intent intent = new Intent(MainActivity.this, VideoChatActivity.class);
                intent.putExtra(Constants.USER_NAME, username);
                intent.putExtra(Constants.CALL_USER, callNum);  // Only accept from this number?
                startActivity(intent);
            }
        });
    } catch (JSONException e) {
        e.printStackTrace();
    }
}