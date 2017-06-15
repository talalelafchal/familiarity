package com.example.SmsService;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.devsmart.android.ui.HorizontalListView;
import com.fedorvlasov.lazylist.ImageLoader;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.squareup.picasso.Picasso;

import javax.microedition.khronos.egl.EGLConfig;
import com.google.vrtoolkit.cardboard.*;
import javax.microedition.khronos.egl.EGLConfig;
import java.nio.FloatBuffer;

public class BroadcastNewSms extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener{
    /**
     * Called when the activity is first created.
     */
    private MediaPlayer mediaPlayer;
    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;

    private MediaPlayer mediaPlayer2;
    private SurfaceHolder vidHolder2;
    private SurfaceView vidSurface2;

    String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";//http://127.0.0.1:59777/smb/192.168.0.250/Files/Interstellar.mp4
    Context context;

    ImageView image_list_icon;
    android.widget.VideoView vidView;
    android.widget.VideoView vidView2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        context = getApplicationContext();

      /*      vidSurface = (SurfaceView)findViewById(R.id.surfaceView);
             vidHolder = vidSurface.getHolder();
             vidHolder.addCallback(this);

        vidSurface2 = (SurfaceView)findViewById(R.id.surfaceView1);
        vidHolder2 = vidSurface2.getHolder();
        vidHolder2.addCallback(this);*/

        vidView = (android.widget.VideoView)findViewById(R.id.videoView2);
        String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        Uri vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);
        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);
        vidView.start();
        vidView2 = (android.widget.VideoView)findViewById(R.id.videoView3);
        String vidAddress2 = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        Uri vidUri2 = Uri.parse(vidAddress);
        vidView2.setVideoURI(vidUri);
        MediaController vidControl2 = new MediaController(this);
        vidControl.setAnchorView(vidView);
        vidView2.setMediaController(vidControl);
        vidView2.start();

    }




    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(vidHolder);
            mediaPlayer.setDataSource(vidAddress);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        }
        catch(Exception e){

            e.printStackTrace();
        }
        try {
            mediaPlayer2 = new MediaPlayer();
            mediaPlayer2.setDisplay(vidHolder2);
            mediaPlayer2.setDataSource(vidAddress);
            mediaPlayer2.prepare();
            mediaPlayer2.setOnPreparedListener(this);
            mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
    }
}
