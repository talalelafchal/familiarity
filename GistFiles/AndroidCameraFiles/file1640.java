package com.fuhoi.android.utils;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

/**
 * LogUtils
 * Helper class for logging using JSON format (other formats to be added as needed)
 * Supports method chaining
 *
 * Typical use:
 *      private LogUtils mLogUtils = new LogUtils(TAG);
 *
 *      @Override
 *      protected void onCreate(Bundle savedInstanceState) {
 *          mLogUtils.setMethod("onCreate").addMessage("start").add("savedInstanceState", savedInstanceState).logToD();
 *          mLogUtils.setMethod("onCreate").addMessage("end").logToD();
 *      }
 *
 * One line log:
 *      new LogUtils(TAG)
 *          .setMethod("onCreate")
 *          .addMessage("start")
 *          .add("savedInstanceState", savedInstanceState)
 *          .logToD();
 *
 * To string for any class
 *      @Override
 *      public String toString() {
 *          return new LogUtils(TAG)
 *              .add("savedInstanceState", savedInstanceState)
 *              .toString();
 *      }
 *
 * @author adaml
 * @date 11/02/2014
 */

public class LogUtils {

    private static final String TAG = LogUtils.class.getSimpleName();

    private static final boolean APPEND_CLAZZ = false;
    private static final boolean APPEND_METHOD = true;

    private static final boolean DEFAULT_ENABLED = true;
    private static final String DEFAULT_DELIMITER = ", ";
    private static final String DEFAULT_PREFIX = "{";
    private static final String DEFAULT_SUFFIX = "}";
    private static final String DEFAULT_FORMAT = "'%s': '%s'";
    private static final String DEFAULT_NULL_TEXT = "null";
    private static final String ELLIPSIZE_TEXT = "...";
    private static final int ELLIPSIZE_TEXT_LENGTH = 3;
    private static final int MAX_LOG_LENGTH = 100;
    private static final int TRUNCATED_LOG_LENGTH = 97; // max length of log minus ELLIPSIZE_TEXT.length()

    private String mClazz;
    private String mMethod;
    private boolean mEnabled;
    private ArrayList<Pair<String, Object>> mPairs = new ArrayList<Pair<String, Object>>();

    public LogUtils() {
        this(null, null, DEFAULT_ENABLED);
    }

    public LogUtils(String clazz) {
        this(clazz, null, DEFAULT_ENABLED);
    }

    public LogUtils(String clazz, boolean enabled) {
        this(clazz, null, enabled);
    }

    public LogUtils(String clazz, String method) {
        this(clazz, method, DEFAULT_ENABLED);
    }

    public LogUtils(String clazz, String method, boolean enabled) {
        setClazz(clazz);
        setMethod(method);
        setEnabled(enabled);
    }

    public LogUtils setClazz(String clazz) {
        mClazz = clazz;
        return this;
    }

    public String getClazz() {
        return mClazz != null ? mClazz : TAG;  // return this class as a default
    }

    public LogUtils setMethod(String method) {
        mMethod = method;
        return this;
    }

    public String getMethod() {
        return mMethod;  // no default case
    }

    public LogUtils setEnabled(boolean enabled) {
        mEnabled = enabled;
        return this;
    }
    
    public boolean getEnabled() {
        return mEnabled;
    }

    public LogUtils addMessage(String message) {
        mPairs.add(new Pair<String, Object>("MESSAGE", message));
        return this;
    }

    public LogUtils add(String key, Object value) {
        mPairs.add(new Pair<String, Object>(key, value));
        return this;
    }

    public LogUtils clear() {
        //mClazz = null;
        mMethod = null;
        //mPairs = new ArrayList<Pair<String, Object>>();
        mPairs.clear();
        return this;
    }

    /**
     * Logs to debug output and clears method and params
     * @return
     */
    public void logToD() {
        if (mEnabled) {
            Log.d(getClazz(), this.toString());  // toString() will clear
        } else {
            clear();
        }
    }

    private String ellipsize(String value) {
        return value.length() > ELLIPSIZE_TEXT_LENGTH + 1 && value.length() > MAX_LOG_LENGTH ?
                value.substring(0, TRUNCATED_LOG_LENGTH).trim() + ELLIPSIZE_TEXT :
                value;
    }

    private String formatString(String key, String value) {
        return String.format(DEFAULT_FORMAT, key, value);
    }

    private String formatString(String key, Object object) {
        if (object != null) {
            /*
             * NOTE: Don't ellipsize container classes (arrays, collections), just their contents
             * Each class should limit the number of items in toString() if required
             * If each class uses this LogUtils class then the format will be correct
             */
            if (object instanceof String) {
                String val = object.toString();
                if (val.startsWith("http")) {  // print HTTP in full to assist debugging
                    return formatString(key, val);
                } else {
                    return formatString(key, ellipsize(val));
                }
            } else {
                return formatString(key, object.toString());
            }
        }
        return formatString(key, DEFAULT_NULL_TEXT);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DEFAULT_PREFIX);

        if (APPEND_CLAZZ) {
            stringBuilder.append(formatString("CLASS", getClazz())).append(DEFAULT_DELIMITER);
        }

        if (APPEND_METHOD) {
            if (mMethod != null) {
                stringBuilder.append(formatString("METHOD", getMethod())).append(DEFAULT_DELIMITER);
            }
        }

        if (mPairs != null && mPairs.size() > 0) {
            for (Pair<String, Object> pair : mPairs) {
                stringBuilder.append(formatString(pair.first, pair.second)).append(DEFAULT_DELIMITER);
            }
        }

        stringBuilder.setLength(stringBuilder.length() - DEFAULT_DELIMITER.length());  // remove last occurrence of delimiter
        stringBuilder.append(DEFAULT_SUFFIX);

        clear();  // clear every time toString is called

        return stringBuilder.toString();
    }
}
