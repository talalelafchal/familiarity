package com.brightcove.player.demo.ootbp;
 
/**
 * This app illustrates the basic behavior of the Android default media controller.  In this case the media controls
 * transition from showing to hidden using a "slide" animation style.
 *
 * @author Paul Michael Reilly
 */
public class MainActivity extends BrightcovePlayer {
 
    // Private class constants
 
    private final String TAG = this.getClass().getSimpleName();
 
    @Override protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.default_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        brightcoveVideoView.setMediaController(new BrightcoveMediaController(brightcoveVideoView));
        super.onCreate(savedInstanceState);
 
        // Add a test video from the res/raw directory to the BrightcoveVideoView.
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        Uri video = Uri.parse("android.resource://" + PACKAGE_NAME + "/" + R.raw.shark);
        brightcoveVideoView.add(Video.createVideo(video.toString()));
        
        // Disable the automatic hiding of the media controls.
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Event.ANIMATION_STYLE, "slide");
        brightcoveVideoView.getEventEmitter().emit(EventType.SHOW_MEDIA_CONTROLS, properties);
    }
 
}