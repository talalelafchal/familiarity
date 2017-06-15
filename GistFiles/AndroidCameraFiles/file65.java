// VideoChatActivity#onCreate()
Bundle extras = getIntent().getExtras();
if (extras == null || !extras.containsKey(Constants.USER_NAME)) {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    Toast.makeText(this, "Need to pass username to VideoChatActivity in intent extras (Constants.USER_NAME).",
            Toast.LENGTH_SHORT).show();
    finish();
    return;
}
this.username  = extras.getString(Constants.USER_NAME, "");