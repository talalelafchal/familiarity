package com.example.jharshman.jdharshmanfinal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jharshman on 12/1/15.
 */

public class GameView extends View {

    final int HEIGHT        = 14;
    final int WIDTH         = 14;
    final int RED           = Color.rgb(255, 0, 0);
    final int BLACK         = Color.rgb(0, 0, 0);
    final int YELLOW        = Color.rgb(205, 255, 0);
    final int WHITE         = Color.rgb(255, 255, 255);
    private Paint mPaint;
    private float mScaledWidth;
    private float mScaledHeight;


    private int[][] gameBoard = {

        {0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,3,0,0,3,0,0,0},
        {0,3,3,3,3,3,3,3,0,0,3,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,3,0,0,0},
        {0,0,0,0,0,3,0,0,0,0,3,0,0,0},
        {0,0,0,0,0,3,0,0,0,0,3,0,0,0},
        {0,0,2,0,0,3,0,1,0,0,0,0,3,0},
        {0,0,0,0,0,3,0,0,0,0,0,0,3,0},
        {0,0,0,0,0,3,0,0,0,0,0,0,3,0},
        {0,0,0,0,0,3,0,0,0,2,0,0,3,0},
        {0,3,3,3,3,3,3,0,0,0,0,0,3,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,3,0},
        {0,0,2,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,3,3,3,3,3},

        /*
        0 = cake
        1 = pacman
        2 = ghost
        3 = wall
         */

    };

    /**
     * Sub constructor for View.  Calls GameView(Context, AttributeSet)
     *
     * @Param   context     the current context
     * */
    public GameView(Context context) {
        this(context, null);
    }

    /**
     * Sub constructor for the View.  Calls GameView(Context, AttributeSet, defStyle)
     *
     * @Param   context         the current context for the view
     * @Param   attributeSet    the passed in set of attributes for the view
     * */
    public GameView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /**
     * Master constructor for the view
     *
     * @Param   context         the current context for the view
     * @Param   attributeSet    the passed in set of attributes for the view
     * @Param   defStyle        the default style to apply to the view
     * */
    public GameView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);

        // View initialization (all view init goes here)

        // get new Paint object
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // set the view background
        setBackgroundResource(R.drawable.background);

    }

    /***/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = (MeasureSpec.getSize(heightMeasureSpec) * 1);
        int height =(MeasureSpec.getSize(widthMeasureSpec) / 1);

        if(height > MeasureSpec.getSize(heightMeasureSpec)) height = MeasureSpec.getSize(heightMeasureSpec);
        else                                                width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //scale the canvas
        float scaleWidth = mScaledWidth/(float)(WIDTH);
        float scaleHeight = mScaledHeight/(float)(HEIGHT);

        canvas.scale(scaleWidth, scaleHeight);

        // todo: draw the game state according to the gameBoard matrix
        for(int i = 0; i < gameBoard[0].length; i++) {
            for(int j = 0; j < gameBoard.length; j++) {
                if(gameBoard[i][j] == 2) {
                    //place ghost at location
                    drawCharacter(canvas, i, j, (float).50, RED);
                } else if(gameBoard[i][j] == 1) {
                    //place pacman at location
                    drawCharacter(canvas, i, j, (float).50, YELLOW);
                } else if(gameBoard[i][j] == 0) {
                    // place cake
                    drawCharacter(canvas, i, j, (float).20, WHITE);
                }
                //todo: else draw barriers
            }
        }

    }

    private void drawCharacter(Canvas canvas, int xDim, int yDim, float radius, int color) {
        // set the color of the character
        mPaint.setColor(color);
        // draw the dot on the map centered at the coordinates given
        // flip the xDim and yDim to preserve the orientation of game board
        canvas.drawCircle(yDim, xDim, radius, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScaledWidth = w;
        mScaledHeight = h;
    }
}