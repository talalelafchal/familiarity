/**
* Copyright (C) 2012 Brightcove, Inc. All Rights Reserved. No
* use, copying or distribution of this work may be made except in
* accordance with a valid license agreement from Brightcove, Inc.
* This notice must be included on all copies, modifications and
* derivatives of this work.
*
* Brightcove, Inc MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT
* THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
* NON-INFRINGEMENT. BRIGHTCOVE SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED
* BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
* SOFTWARE OR ITS DERIVATIVES.
*
* "Brightcove" is a trademark of Brightcove, Inc.
*/
package com.brightcove.player.view;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.event.Component;
import com.brightcove.player.event.Default;
import com.brightcove.player.event.Emits;
import com.brightcove.player.event.ListensFor;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventLogger;
import com.brightcove.player.event.EventType;

import java.util.Map;
import java.util.HashMap;

/**
* An Activity with basic life cycle and full screen support. The
* onCreate() should be extended to wire up the activity's layout to
* the brightcoveVideoView instance variable. For example:
* <code>
* brightcoveVideoView = (BaseVideoView) findViewById(R.id.brightcove_video_view);
* </code>
*/
@Emits(events = {
        EventType.ACTIVITY_CREATED,
        EventType.ACTIVITY_DESTROYED,
        EventType.ACTIVITY_PAUSED,
        EventType.ACTIVITY_RESTARTED,
        EventType.ACTIVITY_RESUMED,
        EventType.ACTIVITY_SAVE_INSTANCE_STATE,
        EventType.ACTIVITY_STARTED,
        EventType.ACTIVITY_STOPPED
})
@ListensFor(events = {
        EventType.ACTIVITY_DESTROYED,
        EventType.ACTIVITY_SAVE_INSTANCE_STATE,
})
public class BrightcovePlayer extends Activity implements Component {
    public static final String TAG = BrightcovePlayer.class.getSimpleName();
    protected static final String POSITION = "position";
    protected static final String WAS_PLAYING = "wasPlaying";

    protected BaseVideoView brightcoveVideoView;
    private int originalLayoutParamsWidth;
    private int originalLayoutParamsHeight;
    private int position;
    private boolean wasPlaying;
    private EventLogger eventLogger;
    private EventEmitter eventEmitter;

    public BrightcoveVideoView getBrightcoveVideoView() {
        BrightcoveVideoView result = null;

        if (brightcoveVideoView instanceof BrightcoveVideoView) {
            result = (BrightcoveVideoView) brightcoveVideoView;
        }

        return result;
    }

    public BaseVideoView getBaseVideoView() {
        return brightcoveVideoView;
    }

    @SuppressLint("NewApi")
    private void hideActionBar() {
        if (Build.VERSION.SDK_INT >= 11) {
            ActionBar actionBar = getActionBar();

            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    @SuppressLint("NewApi")
    private void showActionBar() {
        if (Build.VERSION.SDK_INT >= 11) {
            ActionBar actionBar = getActionBar();

            if (actionBar != null) {
                actionBar.show();
            }
        }
    }

    /**
* Show the closed captioning dialog for making changes to styling options.
* This method should only be called on Android versions prior to KitKat, as
* these settings are built into KitKat and above.
*/
    @SuppressLint("NewApi")
    public void showClosedCaptioningDialog() {
        brightcoveVideoView.getClosedCaptioningController().showCaptionsDialog();
    }

    /**
* Hide the closed captioning dialog for making changes to styling options.
* This method should only be called on Android versions prior to KitKat, as
* these settings are built into KitKat and above.
*/
    @SuppressLint("NewApi")
    public void hideClosedCaptioningDialog() {
        brightcoveVideoView.getClosedCaptioningController().hideCaptionsDialog();
    }

    /**
* Expands the BrightcoveVideoView layout parameters to match the parent and hides the ActionBar.
*/
    public void fullScreen() {
        if (eventEmitter != null && !brightcoveVideoView.isFullScreen()) {
            eventEmitter.emit(EventType.ENTER_FULL_SCREEN);
        } else {
            Log.e(TAG, "Event emitter is not defined or the video view is already in full screen mode.");
        }
    }

    /**
* Returns the BrightcoveVideoView to it's original layout parameters and restores the ActionBar.
*/
    public void normalScreen() {
        if (eventEmitter != null && brightcoveVideoView.isFullScreen()) {
            eventEmitter.emit(EventType.EXIT_FULL_SCREEN);
        } else {
            Log.e(TAG, "Event emitter is not defined or the video view is not in full screen mode!");
        }
    }

    public EventLogger getEventLogger() {
        return eventLogger;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (brightcoveVideoView != null) {
            eventEmitter = brightcoveVideoView.getEventEmitter();

            eventLogger = new EventLogger(eventEmitter, true, getClass().getSimpleName());

            if (savedInstanceState != null) {
                position = savedInstanceState.getInt(POSITION);
                wasPlaying = savedInstanceState.getBoolean(WAS_PLAYING);
            }

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(Event.ACTIVITY, this);
            if (savedInstanceState != null) {
                properties.put(Event.INSTANCE_STATE, savedInstanceState);
            }
            eventEmitter.emit(EventType.ACTIVITY_CREATED, properties);
        } else {
            throw new IllegalStateException("brightcoveVideoView needs to be wired up to the layout.");
        }
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();

        eventEmitter.once(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            @Default
            public void processEvent(Event event) {
                if (position > 0) {
                    brightcoveVideoView.seekTo(position);
                    position = -1;
                }

                if (wasPlaying) {
                    brightcoveVideoView.start();
                    wasPlaying = false;
                }
            }
        });

        eventEmitter.emit(EventType.ACTIVITY_STARTED);
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();

        if (brightcoveVideoView.isPlaying()) {
            brightcoveVideoView.pause();
            wasPlaying = true;
        } else {
            wasPlaying = false;
        }

        eventEmitter.emit(EventType.ACTIVITY_PAUSED);
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();

        if (wasPlaying) {
            brightcoveVideoView.start();
        }

        eventEmitter.emit(EventType.ACTIVITY_RESUMED);
    }

    @Override
    protected void onRestart() {
        Log.v(TAG, "onRestart");
        super.onRestart();

        eventEmitter.emit(EventType.ACTIVITY_RESTARTED);
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();

        // Give all the listeners a chance to run before shutting down the EventEmitter.
        eventEmitter.on(EventType.ACTIVITY_DESTROYED, new EventListener() {
            @Override
            @Default
            public void processEvent(Event event) {
                eventEmitter.off();
            }
        });

        eventEmitter.emit(EventType.ACTIVITY_DESTROYED);
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();

        position = brightcoveVideoView.getCurrentPosition();
        brightcoveVideoView.stopPlayback();
        eventEmitter.emit(EventType.ACTIVITY_STOPPED);
    }

    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        bundle.putInt(POSITION, position);
        bundle.putBoolean(WAS_PLAYING, wasPlaying);

        // Give all the listeners a chance to run before calling super.
        eventEmitter.on(EventType.ACTIVITY_SAVE_INSTANCE_STATE, new EventListener() {
            @Override
            @Default
            public void processEvent(Event event) {
                BrightcovePlayer.super.onSaveInstanceState(bundle);
            }
        });

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Event.INSTANCE_STATE, bundle);
        eventEmitter.emit(EventType.ACTIVITY_SAVE_INSTANCE_STATE, properties);
    }
}