
import android.util.Log;

import java.util.HashMap;

public class Timer {
    private static HashMap<String, Long> measurements = new HashMap<>();

    private Timer(){}

    public static void start(String benchmark){
        measurements.put(benchmark, System.currentTimeMillis());
    }

    public static long end(String benchmark) {
        if(measurements.containsKey(benchmark)) {
            long startTime = measurements.remove(benchmark);
            long elapsedTime = System.currentTimeMillis() - startTime;
            Log.d("MethodTimer", "[" + benchmark + "] Elapsed time: " + elapsedTime + "ms.");
            return elapsedTime;
        }
        return -1;
    }
}
