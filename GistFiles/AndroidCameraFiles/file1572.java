package com.example.demo.cardboard360video;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

public class MainActivity extends CardboardActivity {

    private CardboardView cardboardView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set all views
        setContentView(R.layout.activity_main);
        cardboardView = (CardboardView) findViewById(R.id.cardboard_view);

        // init media player for video
        mediaPlayer = MediaPlayer.create(this, R.raw.test_hd);
        mediaPlayer.setLooping(true);

        // create renderer for all opengl stuff
        CardboardView.StereoRenderer renderer = new VideoPanoramaRenderer(this, mediaPlayer);
        // associate a renderer with cardboardView
        cardboardView.setRenderer(renderer);

        // associate the cardboardView with this activity
        setCardboardView(cardboardView);
    }

    @Override
    public void onCardboardTrigger() {
        super.onCardboardTrigger();

        // toggle vr mode on touch/trigger
        cardboardView.setVRModeEnabled(!cardboardView.getVRMode());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}
