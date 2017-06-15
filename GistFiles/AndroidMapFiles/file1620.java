import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by afleshner on 8/26/2016. To start 
 * String str = "Say Hello world";
 * Intent speakingIntent = new Intent(this,SpeakingService.class);
 * speakingIntent.putExtra(SpeakingService.WHAT_TO_SAY,str);
 * startService(speakingIntent);
 */
public class SpeakingService extends Service implements TextToSpeech.OnInitListener {


    public static final String WHAT_TO_SAY = "What to say key";
    private String say;
    private TextToSpeech mTts;
    private static final String TAG = "TTSService";
    private Load notif;

    @Override

    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public void onCreate() {
        //Register a receiver to stop Service
        registerReceiver(stopServiceReceiver, new IntentFilter("myFilter"));
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, new Intent("myFilter"), PendingIntent.FLAG_UPDATE_CURRENT);
        notif = PugNotification.with(getApplicationContext()).load().identifier(1337).button(R.drawable.ic_close_circle_black_36dp, "Stop Speaking", contentIntent)
                .message("Hello World").smallIcon(R.drawable.ic_pikachu).largeIcon(R.drawable.ic_launcher);
        notif.simple().build();
        mTts = new TextToSpeech(getApplicationContext(),
                this  // OnInitListener
        );
        mTts.setLanguage(Locale.US);
        mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                //Stop the service so it is not running in the background.
                stopSelf();
            }

            @Override
            public void onError(String s) {

            }
        });
        Log.v(TAG, "oncreate_service");
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            say = intent.getStringExtra(WHAT_TO_SAY);
            sayHello(say);
            Log.v(TAG, "onstart_service");
        } catch (NullPointerException e) {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onInit(int status) {
        Log.v(TAG, "oninit");
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Language is not available.");
            } else {
                sayHello(say);
            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }
    }

    private void sayHello(String str) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(str);
        } else {
            ttsUnder20(str);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    //We need to declare the receiver with onReceive function as below
    protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManagerCompat.from(context).cancel(1337);
            stopSelf();
            unregisterReceiver(this);
        }
    };


}