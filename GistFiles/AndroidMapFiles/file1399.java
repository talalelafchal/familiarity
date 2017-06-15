package com.example.bugapp;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.display.SeamlessVideoDisplayComponent;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventLogger;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoFields;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.util.StringUtil;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.visualon.OSMPBasePlayer.voOSBasePlayer;
import com.visualon.OSMPUtils.voOSProgramInfo;
import com.visualon.OSMPUtils.voOSStreamInfo;

public class MainActivity extends BrightcovePlayer {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
        catalog.findVideoByReferenceID("shark", new VideoListener() {
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
            }

            public void onError(String error) {
                Log.e(TAG, error);
            }
        });

        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        eventEmitter.on(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                SeamlessVideoDisplayComponent seamlessVideoDisplayComponent =
                    (SeamlessVideoDisplayComponent) brightcoveVideoView.getVideoDisplay();

                voOSBasePlayer voOSBasePlayer = seamlessVideoDisplayComponent.getSeamlessPlayer();

                voOSProgramInfo voOSProgramInfo = (voOSProgramInfo) voOSBasePlayer.GetProgramInfo(0);
                Log.v(TAG, "program name: " + voOSProgramInfo.getProgramName());
                Log.v(TAG, "program id: " + voOSProgramInfo.getProgramID());
                voOSStreamInfo[] voOSStreamInfos = voOSProgramInfo.getStreamInfo();
                int highestBitrate = 0;
                int highestStreamID = 0;

                for (voOSStreamInfo voOSStreamInfo : voOSStreamInfos) {
                    int streamBitrate = voOSStreamInfo.getBitrate();
                    Log.v(TAG, "bitrate: " + streamBitrate);
                    if (streamBitrate > highestBitrate) {
                        highestBitrate = streamBitrate;
                        highestStreamID = voOSStreamInfo.getStreamID();
                    }
                }

                voOSBasePlayer.SelectStream(highestStreamID);
            }
        });
    }
}
