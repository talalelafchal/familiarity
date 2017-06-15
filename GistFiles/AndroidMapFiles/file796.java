package com.brightcove.android_sdk_quick_start1;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveVideoView;

public class MainActivity extends Activity {
  public static final String TAG = "**VIDEO INFO**";
  EventEmitter eventEmitter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final BrightcoveVideoView bcVideoView = (BrightcoveVideoView) findViewById(R.id.bc_video_view);
    final Catalog catalog = new Catalog("h9Ne4RpQO6O-1IuekDkyR874wALDyZFa63r4pSQQr3ESNL9HANIazA..");
    eventEmitter =  bcVideoView.getEventEmitter();

    MediaController controller = new MediaController(this);
    bcVideoView.setMediaController(controller);

    eventEmitter.on(EventType.CUE_POINT, new EventListener() {

      @Override
      public void processEvent(Event event) {
        Log.d(TAG,"cue point event: " + event);
      }
    });

    catalog.findVideoByReferenceID("bird-for-android-sdk",new VideoListener() {
      @Override
      public void onVideo(Video video) {
        Log.d(TAG, "get cuepoints: " + video.getCuePoints());

        for (CuePoint cuePoint : video.getCuePoints()) {
          Log.d(TAG, "CuePoint name = " + cuePoint.getProperties().get("name"));
        }

        bcVideoView.add(video);
        bcVideoView.start();
      }

      @Override
      public void onError(String error) {
        //Insert error handling here
        Log.e(TAG, error);
      }
    });

    eventEmitter.on(EventType.DID_SET_VIDEO, new EventListener() {
      @Override
      public void processEvent(Event event) {
        setupCuePoints();
      }
    });
  }

  private void setupCuePoints() {
    String cuePointType = "ad";
    Map<String, Object> properties = new HashMap<String, Object>();
    Map<String, Object> details = new HashMap<String, Object>();

    // preroll
    CuePoint cuePoint = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType, properties);
    details.put(Event.CUE_POINT, cuePoint);
    eventEmitter.emit(EventType.SET_CUE_POINT, details);
    Log.d(TAG, "cue point details1: " + details);

    // midroll
    cuePoint = new CuePoint(3000, cuePointType, properties);
    details.put(Event.CUE_POINT, cuePoint);
    eventEmitter.emit(EventType.SET_CUE_POINT, details);
    Log.d(TAG, "cue point details2: " + details);

    // postroll
    cuePoint = new CuePoint(CuePoint.PositionType.AFTER, cuePointType, properties);
    details.put(Event.CUE_POINT, cuePoint);
    eventEmitter.emit(EventType.SET_CUE_POINT, details);
    Log.d(TAG, "cue point details3: " + details);
    Log.d(TAG, "cue point properties" + properties);
  }
}