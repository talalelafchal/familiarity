import android.util.Log;

import java.util.Map;

/**
 *
 */
@SuppressWarnings("unused")
public final class Logger {

    public static final int VERBOSE = Log.VERBOSE;
    public static final int DEBUG = Log.DEBUG;
    public static final int INFO = Log.INFO;
    public static final int WARN = Log.WARN;
    public static final int ERROR = Log.ERROR;
    public static final int ASSERT = Log.ASSERT;

    public static boolean _enabled = true;
    public static int level = VERBOSE;

    private Logger() {
    }

    public static void logMethod(String clazz, String method, Map<String, Object> parameters) {
        final StringBuilder SB = new StringBuilder()
                .append(method != null ? method : "null")
                .append(":")
                .append("\n");
        if (parameters != null)
            for (String key : parameters.keySet())
                if (key != null)
                    SB.append("  ")
                            .append(key)
                            .append("=")
                            .append(parameters.get(key) != null ? parameters.get(key) : "null")
                            .append("\n");
        log(clazz != null ? clazz : "null", SB.toString());
    }

    public static void log(String tag, String message) {
        i(tag != null ? tag : "null", message != null ? message : "null");
    }

    public static void v(String tag, String message) {
        if (check(VERBOSE))
            Log.v(tag, message);
    }

    public static void v(String tag, String message, Throwable tr) {
        if (check(VERBOSE))
            Log.v(tag, message, tr);
    }

    public static void d(String tag, String message) {
        if (check(VERBOSE))
            Log.d(tag, message);
    }

    public static void d(String tag, String message, Throwable tr) {
        if (check(DEBUG))
            Log.d(tag, message, tr);
    }

    public static void i(String tag, String message) {
        if (check(INFO))
            Log.i(tag, message);
    }

    public static void i(String tag, String message, Throwable tr) {
        if (check(INFO))
            Log.i(tag, message, tr);
    }

    public static void w(String tag, String message) {
        if (check(WARN))
            Log.w(tag, message);
    }

    public static void w(String tag, String message, Throwable tr) {
        if (check(WARN))
            Log.w(tag, message, tr);
    }

    public static void w(String tag, Throwable tr) {
        if (check(WARN))
            Log.w(tag, tr);
    }

    public static void e(String tag, String message) {
        if (check(ERROR))
            Log.e(tag, message);
    }

    public static void e(String tag, String message, Throwable tr) {
        if (check(ERROR))
            Log.e(tag, message, tr);
    }

    public static void wtf(String tag, String message) {
        if (check(ASSERT))
            Log.wtf(tag, message);
    }

    public static void wtf(String tag, String message, Throwable tr) {
        if (check(ASSERT))
            Log.wtf(tag, message, tr);
    }

    public static void wtf(String tag, Throwable tr) {
        if (check(ASSERT))
            Log.wtf(tag, tr);
    }

    private static boolean check(int reqLevel) {
        return _enabled && level <= reqLevel;
    }
}
