/**
 * Generalized code from botifier, source code at https://github.com/grimpy/Botifier
 * Provides example methods for sending text over AVRCP from an Android application.
 * AVRCP is the Bluetooth protocol your phone uses to send song information to your car stereo/smartwatch.
 * Only for Android 5.0+
 */

@Override
protected void onCreate(Bundle savedInstanceState) {
  //in activity or service
  mediaSession = new MediaSession(this, "YourAppName");
  audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
  setUpCallBack();
}

private void setUpCallBack() {
  //capture media events like play, stop
  //you don't actually use these callbacks
  //but you have to have this in order to pretend to be a media application
  mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
          MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
  mediaSession.setCallback(new MediaSession.Callback() {
      @Override
      public void onPlay() {
          super.onPlay();
      }

      @Override
      public void onPause() {
          super.onPause();
      }

      @Override
      public void onSkipToNext() {
          super.onSkipToNext();
      }

      @Override
      public void onSkipToPrevious() {
          super.onSkipToPrevious();
      }

      @Override
      public void onStop() {
          super.onStop();
      }
  });
}

public void sendTextOverAVRCP() {
    PlaybackState state = new PlaybackState.Builder()
            .setActions(
                    PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
                            PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
                            PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
            .setState(PlaybackState.STATE_PLAYING, 1, 1, SystemClock.elapsedRealtime())
            .build();
    //set the metadata to send, this is the text that will be displayed
    //if the strings are too long they might be cut off
    //you need to experiment with the receiving device to know max length
    MediaMetadata metadata = new MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, "title")
            .putString(MediaMetadata.METADATA_KEY_ARTIST, "artist")
            .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, "album_artist")
            .putString(MediaMetadata.METADATA_KEY_ALBUM, "album")
            .putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, 123)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, 456)
            .build();
    //setting this active makes the metadata you pass show up
    //other metadata from apps will not be shown
    mediaSession.setActive(true);
    mediaSession.setMetadata(metadata);
    mediaSession.setPlaybackState(state);

}

private void clearText() {
  //if you display text, calling this will stop displaying it
  //if there is another app which is using AVRCP, 
  //control will be handed off that app
  if (mediaSession != null)
      mediaSession.setActive(false);
}