package com.example.TrafficJam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: steinar
 * Date: 3/24/13
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameView extends GridView {


    private class MyShape {
        private int mColor;
        private Rect mRect;
        MyShape( int color, Rect rect) {
            mColor = color;
            mRect = rect;
        }
        public int getColor() {
            return mColor;
        }
        public Rect getRect() {
            return mRect;
        }
    }

    private final int SHAPE_SIZE = 100;
    private Paint mPaint = new Paint();
    private MyShape mMovingShape = null;
    private GameEventHandler mListener = null;

    private List<MyShape> mShapes = new ArrayList<MyShape>();
    private Random mRandom = new Random();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor( Color.WHITE );
        mPaint.setStyle( Paint.Style.FILL_AND_STROKE );
    }


    public void setCustomEventHandler (GameEventHandler listener)
    {
        mListener = listener;
    }



    public void addShape( int color ) {
        Rect rect = new Rect();
        //System.out.println( "W:" + getWidth() + " H: " + getHeight());
        int x = mRandom.nextInt( getWidth() - SHAPE_SIZE );
        int y = mRandom.nextInt( getHeight() - SHAPE_SIZE );
        rect.set( x, y, x + SHAPE_SIZE, y + SHAPE_SIZE );
        mShapes.add( new MyShape(color, rect) );
    }

    protected void onDraw( Canvas canvas ) {
        for ( MyShape shape : mShapes ) {
            mPaint.setColor( shape.getColor() );
            canvas.drawRect( shape.getRect(), mPaint );
        }
    }

    public boolean onTouchEvent( MotionEvent motionEvent ) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        switch ( motionEvent.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                mMovingShape = shapeLocatedOn( x, y );
                break;
            case MotionEvent.ACTION_MOVE:
                if ( mMovingShape != null ) {
                    x = Math.max( 0, Math.min( x, getWidth() - SHAPE_SIZE) );
                    y = Math.max( 0, Math.min( y, getHeight() - SHAPE_SIZE) );
                    y = mMovingShape.getRect().top;
                    mMovingShape.getRect().offsetTo( x, y );
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if ( mMovingShape != null ) {
                    mMovingShape = null;
                    if (mListener != null)
                        mListener.onShapeMoved();
                }
                break;
        }
        return true;
    }

    private MyShape shapeLocatedOn( int x, int y ) {
        for ( MyShape shape : mShapes ) {
            if ( shape.getRect().contains( x, y ) ) {
                return shape;
            }
        }
        return null;
    }
}
