package com.example.myapplication;


import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.Random;

/**
 * Created by 323145 on 13/08/2014.
 */
public class PieChartView extends View {


    public static final String DEBUG_TAG = "PieCHartView";
    //one paint for all figures
    private Paint mPaint = new Paint();
    private Paint mTempPaint = new Paint();
    private SparseArray <PieChartElements> mPieChartTable;
    private android.view.GestureDetector mGestureDetector;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPieChartTable = new SparseArray<PieChartElements>();
        mGestureDetector = new android.view.GestureDetector(context, new MyGestureDetectorListener());

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPieChartTable.clear();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mPieChartTable.size(); i++){
            PieChartElements pi = mPieChartTable.valueAt(i);
            if (pi != null){
                pi.drawPieElement(canvas);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        int mPointerIndex = event.getActionIndex();
        int mPointerId = event.getPointerId(mPointerIndex);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                for (int i=0; i<event.getPointerCount(); i++){
                    PieChartElements pi = mPieChartTable.get(event.getPointerId(i));
                    if (pi != null){
                        pi.set(event.getX(i), event.getY(i), event.getPressure(i));
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                PieChartElements mp = new PieChartElements();
                mp.set(event.getX(mPointerIndex), event.getY(mPointerIndex), event.getPressure(mPointerIndex));
                mPieChartTable.put(mPointerId, mp);
                break;
            /*
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                mPieChartTable.remove(mPointerId);
                break;
            */
            default:
                return super.onTouchEvent(event);
        }
        this.invalidate();
        return true;
    }


    private class MyGestureDetectorListener extends GestureDetector.OnDoubleTapListener{
        @Override
        public boolean onDoubleTap(MotionEvent event) {
            int mPointerIndex = event.getActionIndex();
            int mPointerId = event.getPointerId(mPointerIndex);
            for (int i=0; i<event.getPointerCount(); i++){
                PieChartElements pi = mPieChartTable.get(event.getPointerId(i));
                if (pi != null){
                    if (pi.isSelected(event.getX(i), event.getY(i))) {
                        mPieChartTable.remove(mPointerId);
                    }
                }
            }
            return super.onDoubleTap(event);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            return false;
        }
    }



    private class PieChartElements{

        private float mDrawX;
        private float mDrawY;
        private float mPressure;
        private Paint mPiePaint;
        private int mRadius;
        public static final int RADIUS = 150;

        private PieChartElements() {
            this.mPiePaint = new Paint();
            Random rnd = new Random();
            this.mPiePaint.setARGB(255, 156+ rnd.nextInt(50), 156+ rnd.nextInt(50), 156+ rnd.nextInt(50));
            this.mPiePaint.setMaskFilter(new BlurMaskFilter(20,BlurMaskFilter.Blur.NORMAL));
            this.mPiePaint.setStrokeWidth(20);
            this.mPiePaint.setStyle(Paint.Style.STROKE);
            this.mPiePaint.setAntiAlias(true);
        }

        private PieChartElements(Paint paint){
            this.mPiePaint = new Paint();
            this.mPiePaint.set(paint);
        }

        private void drawPieElement(Canvas canvas){
            canvas.drawCircle(this.getmDrawX(), this.getmDrawY(), this.mRadius, this.getmPiePaint());
            Paint mpt = new Paint();
            mpt.setColor(Color.RED);
            mpt.setAntiAlias(true);
            mpt.setTextSize(15);
            //canvas.drawText("PieChartElement", this.getmDrawX()+100, this.getmDrawY()+100, mpt);
        }

        public void set(float posX, float posY, float pressure){
            this.setmDrawX(posX);
            this.setmDrawY(posY);
            this.setmPressure(pressure);
        }

        /**
         * Defines if pointer co-ordinates are inside the object (circle)
         * @param posX
         * @param posY
         * @return
         */
        public boolean isSelected(float posX, float posY){
            //determine the distance between pos(x,y) and center of the object O(mDrawX, mDrawY)
            //use Pythagoras theorem
            double posToO = Math.hypot((posX - getmDrawX()), (posY - getmDrawY()));
            if ((int)posToO <= getmRadius()) return true;
            return false;
        }

        public void setmDrawX(float mDrawX) {
            this.mDrawX = mDrawX;
        }

        public float getmDrawY() {
            return mDrawY;
        }

        public float getmDrawX() {
            return mDrawX;
        }

        public void setmDrawY(float mDrawY) {
            this.mDrawY = mDrawY;
        }

        public float getmPressure() {
            return mPressure;
        }

        /**
         * Setting pressure automatically updates the radius and the Stroke size of the object
         * @param mPressure
         */
        public void setmPressure(float mPressure) {
            this.setmPressure(mPressure);
            this.setmRadius(RADIUS * (int)mPressure);
            this.mPiePaint.setStrokeWidth(this.getmPressure()*40);
        }

        public Paint getmPiePaint() {
            return mPiePaint;
        }

        public void setmPiePaint(Paint mPiePaint) {
            this.mPiePaint = mPiePaint;
        }

        public int getmRadius() {
            return mRadius;
        }

        public void setmRadius(int mRadius) {
            this.mRadius = mRadius;
        }
    }
}