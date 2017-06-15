//In your Activities: (Replace "someViewDuration" with an appropriate name for each activity)


long appearedAtTime;

@Override
public void onResume() {
    super.onResume();
    appearedAtTime = System.currentTimeMillis();
}


@Override
public void onPause() {
    super.onPause();
    HashMap<String, String> properties = new HashMap<String, String>();
    properties.put("someViewDuration", String.valueOf((System.currentTimeMillis() - appearedAtTime) / 1000));
    KISSmetricsAPI.sharedAPI().set(properties);
}
