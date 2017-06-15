package com.example.multitouch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MultiTouchView extends View {
    
    private final float radius;
    private final Paint paint;
    private final Paint backgroundPaint;
    
    private class LocalTouch {
        
        public boolean active;
        
        public float x, y;
        public int id;
        
        public LocalTouch(){
            active = false;
        }
        
        public void onDown(int id, float x, float y){
            this.id = id;
            this.x = x;
            this.y = y;
            
            active = true;
        }
        
        public void onMove(float x, float y){
            this.x = x;
            this.y = y;
        }
        
        public void onUp(){
            active = false;
        }
    }
    
    private final Map<Integer, LocalTouch> idToTouch;
    
    @SuppressLint("UseSparseArrays")
    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        idToTouch = new HashMap<Integer, LocalTouch>();
        
        float density = getResources().getDisplayMetrics().density;
        radius = density * 50;
        
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3 * density);
        paint.setTextSize(16 * density);
        
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.MAGENTA);
    }
    
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        
        Iterator<LocalTouch> it = idToTouch.values().iterator();
        while(it.hasNext()){
            LocalTouch touch = it.next();
            if(touch.active){
                canvas.drawCircle(touch.x, touch.y, radius, paint);
                canvas.drawText("" + touch.id, touch.x + radius, touch.y, paint);
            }
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        // Get index (can vary for a physical touch across multiple #onTouchEvent() calls) corresponding to the pointer
        final int pointerIndex = MotionEventCompat.getActionIndex(event);
        // Get id (constant throughout physical touch) corresponding to the action
        final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
        
        switch(MotionEventCompat.getActionMasked(event)){
        case MotionEvent.ACTION_POINTER_DOWN:
        case MotionEvent.ACTION_DOWN:
        {
            // Treat all pointers as equal
            // i.e. no distinction between primary and secondary
            
            LocalTouch touch = idToTouch.get(pointerId);
            if(null == touch){
                // First one with this id, create it first
                touch = new LocalTouch();
                idToTouch.put(pointerId, touch);
            }
            touch.onDown(pointerId, event.getX(pointerIndex), event.getY(pointerIndex));
            
            invalidate();
            return true;
        }
        case MotionEvent.ACTION_MOVE:
        {
            // If looking for a specific pointer, use
            // int specificPointerIndex = MotionEventCompat.findPointerIndex(event, specificPointerId);
            
            // Multiple pointer motions may be batched in one #onTouchEvent()
            for(int i = 0; i < event.getPointerCount(); i++){
                LocalTouch touch = idToTouch.get(event.getPointerId(i));
                touch.onMove(event.getX(i), event.getY(i));
            }
            
            invalidate();
            return true;
        }
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_POINTER_UP:
        {
            // Like in DOWN, treat all pointers as equal
            idToTouch.get(pointerId).onUp();
            
            // If you prefer, you could remove the Object, i.e. idToTouch.remove(pointerId), here instead
            // eliminating the need for the LocalTouchEvent#valid flag
            
            invalidate();
            return true;
        }
        }
        
        return false;
    }
}