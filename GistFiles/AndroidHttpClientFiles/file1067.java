package com.demo.youtubeinvideoviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private MyPlaylistEventListener playlistEventListener;
    private YouTubePlayer player;

    float experimentDuration;
    float totalVideoDuration = 0;
    long stallBeginning=0;
    long stallEnding=0;
    Integer numOfVideos=0;
    Integer numOfStallings=0;
    long totalStallingTime=0;
    FileOutputStream outputStreamE;
    //FileOutputStream outputStreamB;
    //FileOutputStream outputStreamU;
    FileOutputStream durations;
    long expStartTime;
    long currentTime;
    long initialDelayStart;
    long initialDelayTotal=0;
    String event;
    float timeInVideo;
    boolean bufferingStarted = true;
    long playlistStartTime;
    List videoids = new ArrayList();
    List playedVideos = new ArrayList();

    String API_BASE_URL = "https://www.googleapis.com/youtube/v3/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
// add your other interceptors …

// add logging as last interceptor
        httpClient.addInterceptor(logging).build();  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        expStartTime = System.currentTimeMillis();
        String filenameE = "events" + expStartTime + ".csv";
        //String filenameB = "buffer" + expStartTime + ".csv";
        //String filenameU = "urls" + expStartTime + ".csv";
        String filenameD = "durations" + expStartTime + ".csv";

        try{
            outputStreamE = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp", filenameE));
            //outputStreamB = new FileOutputStream(new File(dir, filenameB));
            //outputStreamU = new FileOutputStream(new File(dir, filenameU));
            durations = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp", filenameD));

        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);

        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        playlistEventListener = new MyPlaylistEventListener();

        /*Button seekToButton = (Button) findViewById(R.id.start_experiment);
        seekToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int skipToSecs = Integer.valueOf(seekToText.getText().toString());
                //player.seekToMillis(skipToSecs * 1000);
                player.play();
            }
        });*/
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player = player;
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        player.setPlaylistEventListener(playlistEventListener);
        player.setFullscreenControlFlags((YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION |
                YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE));

        player.setFullscreen(true);

        if (!wasRestored) {
            //player.cueVideo("fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
            //player.cueVideos(Arrays.asList("ccAiiGb7S6k", "jcuxUTkWm44", "L1c1zf0_VxU"));
            //player.cueVideos(Arrays.asList("hWN0H_WunP4","G1ubVOl9IBw", "sGbxmsDFVnE", "PLCgXMUa-5E", "8BA5TNJsURs", "9bZkp7q19f0", "n1MAiKB2PFM", "Gvchbkh4jQY",
                    //"V8vejjVgIHg", "1Ozq-QXgBiw"));
            player.cueVideos(Arrays.asList("sGbxmsDFVnE", "sGbxmsDFVnE", "sGbxmsDFVnE", "sGbxmsDFVnE", "sGbxmsDFVnE"));
            videoids.addAll(Arrays.asList("sGbxmsDFVnE", "sGbxmsDFVnE", "sGbxmsDFVnE", "sGbxmsDFVnE", "sGbxmsDFVnE"));
            //player.cuePlaylist("PLrIP9IIcq23PnNqdXQ4yXEJ5tC97j6g6M");
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = "Error initializing YT player!";
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void endExperiment(){
        Intent intent = new Intent(this, EndActivity.class);
        intent.putExtra("numOfVid", numOfVideos);
        intent.putExtra("numOfStallingE", numOfStallings/2);
        intent.putExtra("totalVideoDuration", totalVideoDuration/1000);
        intent.putExtra("experimentDuration", experimentDuration);
        startActivity(intent);
        finish();
        /*try{
            saveFiles();
        }
        catch (IOException e){
            e.printStackTrace();
        }*/
    }
    public void saveFiles() throws IOException {

        outputStreamE.close();
        //outputStreamB.close();
        //outputStreamU.close();
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            currentTime = System.currentTimeMillis();
            event = "Playing";
            timeInVideo = (float)player.getCurrentTimeMillis()/1000;
            if(player.getCurrentTimeMillis()<=0.01)
                initialDelayTotal += currentTime-initialDelayStart;

            try {
                if(numOfVideos<=0 || numOfVideos>10) {
                    outputStreamE.write((currentTime + "," + videoids.get(0) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                    if(player.getCurrentTimeMillis()<=0.01)
                        durations.write((currentTime + "," + videoids.get(0) + "This initial delay duration: " + (currentTime-initialDelayStart) + "\n").getBytes());
                }
                else {
                    //outputStreamE.write((event + ":" + currentTime + "\n").getBytes());
                    outputStreamE.write((currentTime + "," + videoids.get(numOfVideos - 1) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                    if(player.getCurrentTimeMillis()<=0.01)
                        durations.write((currentTime + "," + videoids.get(numOfVideos-1) + "This initial delay duration: " + (currentTime-initialDelayStart) + "\n").getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            currentTime = System.currentTimeMillis();
            event = "Paused";
            timeInVideo = (float)player.getCurrentTimeMillis()/1000;
            try {
                if(numOfVideos<=0 || numOfVideos>10) {
                    outputStreamE.write((currentTime + "," + videoids.get(0) + ","+ timeInVideo + "," + event + "\n").getBytes());
                }
                else {
                    if(numOfVideos<=0 || numOfVideos>10) {
                        outputStreamE.write((currentTime + "," + videoids.get(0) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                    }
                    else {
                        //outputStreamE.write((event + ":" + currentTime + "\n").getBytes());
                        outputStreamE.write((currentTime + "," + videoids.get(numOfVideos - 1) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            currentTime = System.currentTimeMillis();
            event = "Stopped";
            timeInVideo = (float) player.getCurrentTimeMillis()/1000;
            try {
                if(numOfVideos<=0 || numOfVideos>10) {
                    outputStreamE.write((currentTime + "," + videoids.get(0) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                }
                else{
                    outputStreamE.write((currentTime + "," + videoids.get(numOfVideos - 1) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
            currentTime = System.currentTimeMillis();
            String event;

            if(bufferingStarted) {
                event = "Buffering started";
                stallBeginning = currentTime;
                bufferingStarted = false;
            }
            else{
                event = "Buffering ended";
                stallEnding = currentTime;
                bufferingStarted = true;
            }
            if(player.getCurrentTimeMillis()<=0.01){
                event += " (beginning)";
                if(numOfVideos==1 && !bufferingStarted){
                        playlistStartTime= System.currentTimeMillis();
                }
                if(!bufferingStarted){
                    initialDelayStart = currentTime;
                }

            }

            else{
                numOfStallings++;
                if(bufferingStarted){
                    totalStallingTime += stallEnding - stallBeginning;
                    try {
                        durations.write((currentTime + "," + videoids.get(numOfVideos-1) + "This stalling duration: " + (stallEnding-stallBeginning) + "\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            timeInVideo = (float)player.getCurrentTimeMillis()/1000;
            try {
                if(numOfVideos<=0 || numOfVideos>10) {
                    outputStreamE.write((currentTime + "," + videoids.get(0) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                }
                else {
                    outputStreamE.write((currentTime + "," + videoids.get(numOfVideos - 1) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }


    }

    private final class MyPlaylistEventListener implements YouTubePlayer.PlaylistEventListener {

        @Override
        public void onPlaylistEnded(){
            String event;
            currentTime = System.currentTimeMillis();
            experimentDuration = (float)(System.currentTimeMillis() - playlistStartTime)/1000;
            event = "Total time [s]";
            try {
                durations.write((currentTime + "," + event +":"+experimentDuration+ "\n").getBytes());
                durations.write((currentTime + "," + "Total stalling duration [ms]" +":"+totalStallingTime+ "\n").getBytes());
                durations.write((currentTime + "," + "Total initial delay duration [ms]" +":"+initialDelayTotal+ "\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            endExperiment();

            /*TextView stalling = (TextView) findViewById(R.id.numOfStall);
            stalling.setText("Number of stalling events: " + (numOfStallings/2));
            TextView numOfVidsT = (TextView) findViewById(R.id.numOfVids);
            numOfVidsT.setText("Number of videos: " + (numOfVideos));*/
        }

        @Override
        public void onPrevious(){

        }


        @Override
        public void onNext(){
            currentTime = System.currentTimeMillis();
            event = "Next video";
            timeInVideo = (float)player.getCurrentTimeMillis()/1000;
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            //showMessage("Loading");
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()

            currentTime = System.currentTimeMillis();
            event = "Loading";
            timeInVideo = (float)player.getCurrentTimeMillis()/1000;

        }

        @Override
        public void onLoaded(String s) {
            //showMessage("Loaded");
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
            currentTime = System.currentTimeMillis();
            event = "Loaded";
            timeInVideo = (float)player.getCurrentTimeMillis()/1000;

        }

        @Override
        public void onAdStarted() {
            //showMessage("AD");
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
            //showMessage("Video started");
            currentTime = System.currentTimeMillis();
            event = "Started";
            if(numOfVideos==0 && playedVideos.size()==0){
                try {
                    Socket s = new Socket("192.168.1.101", 12345);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                numOfVideos++;
                totalVideoDuration += player.getDurationMillis();
                playedVideos.add(videoids.get(0));

            }
            else{
                //if(!playedVideos.contains(videoids.get(numOfVideos))) {
                    try {
                        Socket s = new Socket("192.168.1.101", 12345);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    totalVideoDuration += player.getDurationMillis();
                    numOfVideos++;
                    playedVideos.add(videoids.get(numOfVideos - 1));



            }
            timeInVideo = (float)player.getCurrentTimeMillis()/1000;
            try {
                if(numOfVideos<=0) {
                    outputStreamE.write((currentTime + "," + videoids.get(0) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                }
                else {
                    //outputStreamE.write((event + ":" + currentTime + "\n").getBytes());
                    outputStreamE.write((currentTime + "," + videoids.get(numOfVideos - 1) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
            currentTime = System.currentTimeMillis();
            event = "Ended";
            timeInVideo = player.getCurrentTimeMillis()/1000;
            try {
                if(numOfVideos<=0 || numOfVideos>10) {
                    outputStreamE.write((currentTime + "," + videoids.get(0) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                }
                else {
                    outputStreamE.write((currentTime + "," + videoids.get(numOfVideos - 1) + ","+ timeInVideo + "," + event +  "\n").getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }


}
