// fullscreen mode
brightcoveVideoView.getEventEmitter().emit(EventType.ENTER_FULL_SCREEN);
brightcoveVideoView.getEventEmitter().emit(EventType.EXIT_FULL_SCREEN);



// thumbnail images
String thumbnailUrl = video.getProperties().get("thumbnail").toString();
String videoStillUrl = video.getProperties().get("stillImageUri").toString();

Catalog catalog = new Catalog("");
Map props = new HashMap();
props.put("video_fields", "accountId,name,shortDescription,longDescription,videoStillURL,thumbnailURL,referenceId,id,length,customFields,HLSURL,videoFullLength,cuePoints");
  catalog.findVideoByID("3951601515001", props, new VideoListener() {
    @Override
    public void onVideo(Video video) {
      String thumbnailUrl = video.getProperties().get("thumbnailURL").toString();
      ...

String videoStillUrl = video.getProperties().get("videoStillURL").toString();

// Using single videos
brightcoveVideoView.clear();

catalog.findVideoByID("123456789", new VideoListener() {
  @Override
  public void onVideo(final Video video) {
      brightcoveVideoView.add(video);
      brightcoveVideoView.start();
    
  }
});



catalog.findVideoByID("", new VideoListener() {
  @Override
  public void onVideo(final Video video) {
    eventEmitter.once(EventType.DID_CHANGE_LIST, new EventListener() {
      @Override
      public void processEvent(Event event)
        { brightcoveVideoView.add(video); brightcoveVideoView.start(); }

      });
    brightcoveVideoView.clear();
  }
});



// using a playlist
brightcoveVideoView.pause();
brightcoveVideoView.stopPlayback();
brightcoveVideoView.setCurrentIndex(0);



// captions and themes
<?xml version="1.0" encoding="utf-8"?>
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.brightcove.player"
    android:versionCode="1"
    android:versionName="1.0">

    <application>
        <!-- If we don't register this Activity in the Manifest, apps using the SDK will crash when they try to access it. -->
        <!-- During the app's build process, this manifest will be merged with the app-level one. -->
        <activity android:name="com.brightcove.player.captioning.BrightcoveCaptionPropertiesActivity"/>
    </application>
</manifest>


<activity
    android:name="com.brightcove.player.captioning.BrightcoveCaptionPropertiesActivity"
    android:theme="@style/MyCustomCaptionSettingsTheme"/>
    
    
    

// Playing local MP4 videos
String PACKAGE_NAME = getApplicationContext().getPackageName();
Uri video = Uri.parse("android.asset://" + PACKAGE_NAME + "/" + R.video.getting_started);
brightcoveVideoView.add(Video.createVideo(video.toString()));
brightcoveVideoView.start();




// Displaying current video still when resuming app
@Override
public void onPause() {
    super.onPause();
    mSavePosition = mBccVideoView.getCurrentPosition();
    mBccVideoView.pause(); 
}
@Override
public void onResume() {
    super.onResume();
    if (mSavePosition > 0) {
        mBccVideoView.seekTo(mSavePosition);
    }
}



// google analytics
// Obtain the shared Tracker instance
AnalyticsApplication application = (AnalyticsApplication) getApplication();
tracker = application.getDefaultTracker();



// set default captions
brightcoveVideoView.getEventEmitter().once(EventType.CAPTIONS_LANGUAGES, new EventListener() {
    @Override
    public void processEvent(Event event) {
        brightcoveVideoView.setClosedCaptioningEnabled(true);
        brightcoveVideoView.setSubtitleLocale("fr");
    }
});




// switching between videos in a playlist
private void setupControls(List<Video> videos) {
    previousVideoButton = (Button) findViewById(R.id.previous_video_button);
    nextVideoButton = (Button) findViewById(R.id.next_video_button);
        
    if (videos != null) {
        previousVideoButton.setEnabled(false);
        previousVideoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int index = brightcoveVideoView.getCurrentIndex();
                int size = brightcoveVideoView.getList().size();
                previousVideoButton.setEnabled(index > 1);
                nextVideoButton.setEnabled((index + 1) < size);
                        
                if (index > 0) {
                    brightcoveVideoView.setCurrentIndex(index - 1);
                }
            }
        });

        nextVideoButton.setEnabled(videos.size() > 1);
        nextVideoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int index = brightcoveVideoView.getCurrentIndex();
                int size = brightcoveVideoView.getList().size();
                previousVideoButton.setEnabled(index >= 0);
                nextVideoButton.setEnabled((index + 2) < size);

                if ((index + 1) < size) {
                    brightcoveVideoView.setCurrentIndex(index + 1);
                }
            }
        });
    }
}




// get duration for live stream
brightcoveVideoView.getEventEmitter().on(EventType.PROGRESS, new EventListener() {
   @Override
   public void processEvent(Event event) {
       int duration = (int) event.properties.get(Event.MAX_POSITION);
   }
});




// start playback from middle of video
catalog.findVideoByID(getString(R.string.videoId), new VideoListener() {
    @Override
    public void onVideo(Video video) {
        Log.v(TAG, "onVideo: video = " + video);

        brightcoveVideoView.getEventEmitter().on(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                brightcoveVideoView.seekTo(60000);
                brightcoveVideoView.start();
            }
        });
        brightcoveVideoView.add(video);
        brightcoveVideoView.pause();
    }
});



// manually load DRM content
import com.brightcove.player.display.WidevineMediaDrmCallback;

Video video = Video.createVideo("https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd");
video.getProperties().put(WidevineMediaDrmCallback.DEFAULT_URL, "https://proxy.uat.widevine.com/proxy?video_id=&provider=widevine_test");
brightcoveVideoView.add(video);
brightcoveVideoView.start();


