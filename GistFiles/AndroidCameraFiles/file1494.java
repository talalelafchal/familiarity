// in BackgroundGif subclass
protected void onPostExecute(String params) {
  Log.d(TAG, "creating card with params: " + params);
  if (PlatformUtils.isGlass()) {
    if (null == params)
      return;

    // http://stackoverflow.com/a/21843601/974800
    Uri imgUri = Uri.fromFile(new File(params));

    GGMainActivity.gifFile = params;

    // create card
    Card gifCard = new Card(getActivity());
    if (null != imgUri) {
      gifCard.addImage(imgUri);
      gifCard.setImageLayout(Card.ImageLayout.FULL);
      gifCard.setText(MainActivity.gifFile);
    } else
      gifCard.setText("failed to get image uri");

    // menus currently not supported
    // https://code.google.com/p/google-glass-api/issues/detail?id=320

    TimelineManager tlm = TimelineManager.from(getActivity());
    tlm.insert(gifCard);
    Log.d(TAG, "inserted into timeline!");
  }

  flowControl.startDisplay();
}