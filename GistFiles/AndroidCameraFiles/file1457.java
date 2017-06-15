
import android.content.Context;
import android.os.Vibrator;

import java.util.List;


public class VibrateEngine {
    public VibrateEngine(final List<Morse> currentRythm, final Context context) {
        new Thread() {
            @Override
            public void run() {
//                    this.setDaemon(true);
                synchronized (this.getClass()) {
                    try {
//                        List<Morse> currentRythm = MainActivity.this.rythms.get(detectedEvent);
                        for (Morse currentSignal : currentRythm) {
                            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            long timeMilliSeconds = currentSignal.getDuration();
                            v.vibrate(timeMilliSeconds);

                            Thread.sleep(timeMilliSeconds);
                            Thread.sleep(250); // replace hardcode with Constants
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}