package se.hellsoft.handlerthreadingdemo;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class MyService extends Service implements Handler.Callback {
    private static final int MSG_READ_NEXT_MESSAGE = 101;
    private static final int MSG_UPDATE_UI = 202;
    private static final int MSG_UPDATE_PROGRESS = 303;
    private static final int MSG_CLOSE_COMMUNICATION = 404;
    private static final int MSG_WRITE_MESSAGE = 505;

    private static final UUID DEVICE_ID = UUID.fromString("");
    private static final int BLOCKING_MESSAGE = 1001;
    private static final int MSG_FETCH_BATTERY_STATUS = 606;

    private final LocalBinder mBinder = new LocalBinder();
    private Handler mMainHandler;
    private Handler mReaderHandler;
    private final Set<CardReaderCallback> mCallbacks = new HashSet<>();
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private String mDeviceAddress;
    private InputStream mInputStream;
    private byte[] mBuffer = new byte[64];
    private byte[] mLastMessage;
    private Handler mWriterHandler;
    private OutputStream mOutputStream;
    private CountDownLatch mCountDownLatch;
    private Semaphore mSemaphore;

    public class LocalBinder extends Binder {

        public MyService getservice() {
            return MyService.this;
        }
    }
    private boolean mConnected = false;

    public static void connectToReader(Context context) {
        Intent intent = new Intent(context, MyService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSemaphore = new Semaphore(1);
        mMainHandler = new Handler(Looper.getMainLooper(), this);
        HandlerThread readerThread = new HandlerThread("reader-thread");
        readerThread.start();
        mReaderHandler = new Handler(readerThread.getLooper(), this);
        HandlerThread writerThread = new HandlerThread("writer-thread");
        writerThread.start();
        mWriterHandler = new Handler(writerThread.getLooper(), this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mCallbacks.clear();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connect();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectReader();
        mReaderHandler.removeCallbacksAndMessages(null);
        mReaderHandler.getLooper().quit();
        mMainHandler.removeCallbacksAndMessages(null);
    }

    private void connect() {
        if(!mConnected) {
            try {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                mBluetoothDevice = bluetoothAdapter.getRemoteDevice(mDeviceAddress);
                if(!bluetoothAdapter.getBondedDevices().contains(mBluetoothDevice)) {
                    // ERROR!
                    return;
                }
                
                mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(DEVICE_ID);
                mBluetoothSocket.connect();
                mConnected = true;
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                // TODO Handle error..
            }
        }
    }

    public byte[] readNextMessage() {
        try {
            int bytesRead;
            int totalBytesRead = 0;
            while((bytesRead = mInputStream.read(mBuffer, totalBytesRead, mBuffer.length - totalBytesRead)) != -1) {
                totalBytesRead += bytesRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mBuffer;
    }

    @MainThread
    public void disconnectReader() {
        // Should run stuff on a bg thread...
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if(!mConnected) return;

                try {
                    mBluetoothSocket.close();
                    mConnected = false;
                } catch (IOException e) {
                    // Ignore errors!
                }
            }
        });
    }

    @MainThread
    public void addCallback(CardReaderCallback callback) {
        mCallbacks.add(callback);
    }

    @MainThread
    public void removeCallback(CardReaderCallback callback) {
        mCallbacks.remove(callback);
    }

    @MainThread
    public void sendReaderMessage(byte[] readerMessage) {
        mWriterHandler.obtainMessage(MSG_WRITE_MESSAGE, readerMessage).sendToTarget();
    }

    public void fetchBatteryStatus() {
        mWriterHandler.removeMessages(MSG_FETCH_BATTERY_STATUS);
        mWriterHandler.sendEmptyMessageDelayed(MSG_FETCH_BATTERY_STATUS, 1000);
    }

    @MainThread
    public int getPinsEntered() {
        return 0;
    }

    @Nullable
    @MainThread
    public CardReaderState getCurrentCardReaderState() {
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_READ_NEXT_MESSAGE:
                byte[] dataMsg = readNextMessage();
                int msgType = dataMsg[0];
                if(msgType == BLOCKING_MESSAGE) {
                    mSemaphore.release();
                }
                mMainHandler.obtainMessage(MSG_UPDATE_UI, dataMsg).sendToTarget();
                mReaderHandler.sendEmptyMessage(MSG_READ_NEXT_MESSAGE);
                break;
            case MSG_UPDATE_UI:
                mLastMessage = (byte[]) msg.obj;
                for (CardReaderCallback callback : mCallbacks) {
                    callback.onMessage((byte[]) msg.obj);
                }
                break;
            case MSG_WRITE_MESSAGE:
                mSemaphore.acquire();
                try {
                    byte[] msgToSend = (byte[]) msg.obj;
                    mOutputStream.write(msgToSend);
                    if(msg.arg1 != BLOCKING_MESSAGE) {
                        mSemaphore.release();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    public interface CardReaderCallback {
        @MainThread
        void onMessage(byte[] data);

        @MainThread
        void onCardReaderStateUpdated(CardReaderState state);
    }

    public static class CardReaderState {
    }
}
