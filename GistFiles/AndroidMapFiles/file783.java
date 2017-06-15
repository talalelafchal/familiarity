package com.example.multitouch;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class MultiTouchView extends View {

    private Map<Integer, PointF> fingers = new HashMap<>();

    public MultiTouchView(Context context) {
        super(context);
    }

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            int index = event.getActionIndex();
            int id = event.getPointerId(index);
            PointF finger = fingers.get(id);
            if (finger == null) {
                fingers.put(id, new PointF(event.getX(index), event.getY(index)));
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            int index = event.getActionIndex();
            int id = event.getPointerId(index);
            PointF finger = fingers.get(id);
            if (finger != null) {
                fingers.remove(id);
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            int count = event.getPointerCount();
            for (int index = 0; index < count; index++) {
                PointF finger = fingers.get(event.getPointerId(index));
                if (finger != null) {
                    finger.set(event.getX(index), event.getY(index));
                }
            }
        } else if (action == MotionEvent.ACTION_CANCEL) {
            fingers.clear();
        }

        // do something with fingers and invalidate();

        return true;
    }
}
