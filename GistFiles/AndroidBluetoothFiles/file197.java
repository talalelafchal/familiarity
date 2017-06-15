package se.hellsoft.handlerthreadingdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Handler.Callback, View.OnClickListener,
    ServiceConnection, MyService.CardReaderCallback {

    private static final int MSG_BG_WORK = 101;
    private static final int MSG_UPDATE_UI = 202;
    private static final int MSG_UPDATE_PROGRESS = 303;
    private Handler mUiHandler;
    private Handler mBgHandler;
    private Button mButton;
    private MyService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.my_button);
        if (mButton != null) {
            mButton.setOnClickListener(this);
        }
        mUiHandler = new Handler(Looper.getMainLooper(), this);
        HandlerThread handlerThread = new HandlerThread("bg-thread");
        handlerThread.start();
        mBgHandler = new Handler(handlerThread.getLooper(), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyService.connectToReader(this);
        bindService(new Intent(this, MyService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mService.removeCallback(this);
        mUiHandler.removeCallbacksAndMessages(null);
        mService = null;
        unbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBgHandler.removeCallbacksAndMessages(null);
        mBgHandler.getLooper().quit();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_BG_WORK:
                int progress = doTheBackgroundWork();
                mBgHandler.sendEmptyMessageDelayed(MSG_UPDATE_UI, 5000);
                mUiHandler.obtainMessage(MSG_UPDATE_PROGRESS, progress, 0).sendToTarget();
                break;
            case MSG_UPDATE_UI:
                ((TextView) findViewById(R.id.my_text)).setText(String.format("Progress %d", msg.arg1));
                break;
        }
        return true;
    }

    private int doTheBackgroundWork() {
        SystemClock.sleep(5000);
        return 1;
    }

    @Override
    public void onClick(View v) {
        mService.readNextMessage();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = ((MyService.LocalBinder) service).getservice();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // Will never be called
    }

    @Override
    public void onMessage(byte[] data) {
        ((TextView) findViewById(R.id.my_text)).setText("asdfsadf");
    }
}
