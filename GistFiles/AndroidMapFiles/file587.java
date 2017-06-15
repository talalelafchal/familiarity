/* stolen from input command
* but have some limitation:
* since it can use "android.permission.INJECT_EVENTS", (only system apps can do that)
* Inject event to other apps will raise a exception of no permission
*/

import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.InputEvent;
import android.content.Context;

import java.lang.reflect.Method;

/**
 * Command that sends key events to the device, either by their keycode, or by
 * desired character output.
 */

public class Input {
    private static final String TAG = "Input";

    private static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0; // see InputDispatcher.h
    private static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;  // see InputDispatcher.h
    private static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;  // see InputDispatcher.h
    private Context mContext;

    public Input(Context context) {
        mContext = context;
    }

    /* public method */
    public void text(String text) {
        sendText(InputDevice.SOURCE_KEYBOARD, text);
    }

    public void keyEvent(int keyCode, boolean longpress) {
        sendKeyEvent(InputDevice.SOURCE_KEYBOARD, keyCode, longpress);
    }

    public void keyEvent(int keyCode) {
        keyEvent(keyCode, false);
    }

    public void tap(float x, float y) {
        sendTap(InputDevice.SOURCE_TOUCHPAD, x, y);
    }

    public void press() {
        sendTap(InputDevice.SOURCE_TRACKBALL, 0.0f, 0.0f);
    }

    public void swipe(float x1, float y1, float x2, float y2, int duration) {
        sendSwipe(InputDevice.SOURCE_TOUCHPAD, x1, y1, x2, y2, duration);
    }

    public void swipe(float x1, float y1, float x2, float y2) {
        swipe(x1, y1, x2, y2, -1);
    }

    public void roll(float dx, float dy) {
        sendMove(InputDevice.SOURCE_TRACKBALL, dx, dy);
    }

    /**
     * Convert the characters of string text into key event's and send to
     * device.
     *
     * @param text is a string of characters you want to input to the device.
     */
    private void sendText(int source, String text) {

        StringBuffer buff = new StringBuffer(text);

        boolean escapeFlag = false;
        for (int i=0; i<buff.length(); i++) {
            if (escapeFlag) {
                escapeFlag = false;
                if (buff.charAt(i) == 's') {
                    buff.setCharAt(i, ' ');
                    buff.deleteCharAt(--i);
                }
            }
            if (buff.charAt(i) == '%') {
                escapeFlag = true;
            }
        }

        char[] chars = buff.toString().toCharArray();

        KeyCharacterMap kcm = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
        KeyEvent[] events = kcm.getEvents(chars);
        for(int i = 0; i < events.length; i++) {
            KeyEvent e = events[i];
            e.setSource(source);
            injectKeyEvent(e);
        }
    }

    private void sendKeyEvent(int inputSource, int keyCode, boolean longpress) {
        long now = SystemClock.uptimeMillis();
        injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, inputSource));
        if (longpress) {
            injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 1, 0,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, 0, KeyEvent.FLAG_LONG_PRESS,
                    inputSource));
        }
        injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, inputSource));
    }

    private void sendTap(int inputSource, float x, float y) {
        long now = SystemClock.uptimeMillis();
        injectMotionEvent(inputSource, MotionEvent.ACTION_DOWN, now, x, y, 1.0f);
        injectMotionEvent(inputSource, MotionEvent.ACTION_UP, now, x, y, 0.0f);
    }

    private void sendSwipe(int inputSource, float x1, float y1, float x2, float y2, int duration) {
        if (duration < 0) {
            duration = 300;
        }
        long now = SystemClock.uptimeMillis();
        injectMotionEvent(inputSource, MotionEvent.ACTION_DOWN, now, x1, y1, 1.0f);
        long startTime = now;
        long endTime = startTime + duration;
        while (now < endTime) {
            long elapsedTime = now - startTime;
            float alpha = (float) elapsedTime / duration;
            injectMotionEvent(inputSource, MotionEvent.ACTION_MOVE, now, lerp(x1, x2, alpha),
                    lerp(y1, y2, alpha), 1.0f);
            now = SystemClock.uptimeMillis();
        }
        injectMotionEvent(inputSource, MotionEvent.ACTION_UP, now, x2, y2, 0.0f);
    }

    /**
     * Sends a simple zero-pressure move event.
     *
     * @param inputSource the InputDevice.SOURCE_* sending the input event
     * @param dx change in x coordinate due to move
     * @param dy change in y coordinate due to move
     */
    private void sendMove(int inputSource, float dx, float dy) {
        long now = SystemClock.uptimeMillis();
        injectMotionEvent(inputSource, MotionEvent.ACTION_MOVE, now, dx, dy, 0.0f);
    }

    private void injectKeyEvent(KeyEvent event) {
        Log.i(TAG, "injectKeyEvent: " + event);
        injectInputEvent(event);
    }

    /**
     * Builds a MotionEvent and injects it into the event stream.
     *
     * @param inputSource the InputDevice.SOURCE_* sending the input event
     * @param action the MotionEvent.ACTION_* for the event
     * @param when the value of SystemClock.uptimeMillis() at which the event happened
     * @param x x coordinate of event
     * @param y y coordinate of event
     * @param pressure pressure of event
     */
    private void injectMotionEvent(int inputSource, int action, long when, float x, float y, float pressure) {
        final float DEFAULT_SIZE = 1.0f;
        final int DEFAULT_META_STATE = 0;
        final float DEFAULT_PRECISION_X = 1.0f;
        final float DEFAULT_PRECISION_Y = 1.0f;
        final int DEFAULT_DEVICE_ID = 0;
        final int DEFAULT_EDGE_FLAGS = 0;
        MotionEvent event = MotionEvent.obtain(when, when, action, x, y, pressure, DEFAULT_SIZE,
                DEFAULT_META_STATE, DEFAULT_PRECISION_X, DEFAULT_PRECISION_Y, DEFAULT_DEVICE_ID,
                DEFAULT_EDGE_FLAGS);
        event.setSource(inputSource);
        Log.i(TAG, "injectMotionEvent: " + event);
        injectInputEvent(event);
    }

    private void injectInputEvent(InputEvent event) {
        // inject using reflect
        InputManager im = (InputManager) mContext.getSystemService(Context.INPUT_SERVICE);

        Class[] paramTypes = new Class[2];
        paramTypes[0] = InputEvent.class;
        paramTypes[1] = Integer.TYPE;

        Object[] params = new Object[2];
        params[0] = event;
        params[1] = INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH;

        try {
            Method hiddenMethod = im.getClass().getMethod("injectInputEvent", paramTypes);
            hiddenMethod.invoke(im, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final float lerp(float a, float b, float alpha) {
        return (b - a) * alpha + a;
    }
}