package com.skt.vux.brandon.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;

import com.skt.vux.brandon.app.control.BluetoothClient;
import com.skt.vux.brandon.app.control.BrandonActivity;
import com.skt.vux.brandon.app.control.Command;
import com.skt.vux.brandon.app.control.CommandHandler;
import com.skt.vux.brandon.app.service.QuickstartPreferences;

import java.io.File;
import java.io.IOException;

/**
 * Created by brandon Lee (sylee@in-soft.co.kr) on 2015-11-11.
 */
public class MediaPlayerActivity extends BrandonActivity {
    private final String TAG = MediaPlayerActivity.class.getName();
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button stopButton;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private VUXApplication vuxApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_player);
        vuxApplication = (VUXApplication) getApplicationContext();

        surfaceView = (SurfaceView) findViewById(R.id.media_player);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceHolderCallback);

        stopButton = (Button) findViewById(R.id.btn_media_player_stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUI(9999);
            }
        });
        registerReceiver(broadcastReceiver, new IntentFilter(QuickstartPreferences.MEDIA_PLAY_FINISH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mediaController.show(0);
        return true;
    }

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            surfaceHolder.setKeepScreenOn(true);
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(preparedListener);
                mediaPlayer.setOnErrorListener(errorListener);
                mediaPlayer.setOnCompletionListener(completionListener);
                mediaPlayer.setDisplay(holder);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/media720.mp4");
                ParcelFileDescriptor fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                mediaPlayer.setDataSource(fd.getFileDescriptor());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            holder.removeCallback(this);
        }
    };

    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaController = new MediaController(MediaPlayerActivity.this);
            mediaController.setMediaPlayer(mediaPlayerControl);
            mediaController.setAnchorView(findViewById(R.id.media_controller));
            mp.start();
        }
    };

    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            sendBroadcast(new Intent(QuickstartPreferences.MEDIA_PLAY_FINISH));
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO 로봇 준비자세!
        BluetoothClient.getInstance().sendCommand(Short.parseShort("1"));
        // TODO 로봇 동작 반복?? 랜던??
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (null != mediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    protected Handler.Callback getUiChanger() {
        return new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 9999:
                        finish();
                        break;
                    default:
                        break;
                }
                return false;
            }
        };
    }

    /**
     * 자동 종료를 위한 Broadcast Receiver
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new CommandHandler().send(new Command() {
                @Override
                public void execute() {
                    finish();
                }
            }, 250);
        }
    };

    private MediaController.MediaPlayerControl mediaPlayerControl = new MediaController.MediaPlayerControl() {
        @Override
        public void start() {
            mediaPlayer.start();
        }

        @Override
        public void pause() {
            mediaPlayer.pause();
        }

        @Override
        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        @Override
        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        @Override
        public void seekTo(int pos) {
            mediaPlayer.seekTo(pos);
        }

        @Override
        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return true;
        }

        @Override
        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            return 0;
        }
    };
}
