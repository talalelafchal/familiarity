package com.paad.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TextView;

import com.paad.todolist.todolist.R;

/**
 * Created by WangWei on 2014/12/10.
 */
public class ToDoListItemView extends TextView {


    private Paint marginPaint;
    private Paint linePaint;
    private int paperColor;
    private float margin;

    public ToDoListItemView(Context context) {
        super(context);
        init();
    }

    public ToDoListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToDoListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Resources myResources = getResources();
        marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        marginPaint.setColor(myResources.getColor(R.color.notepad_margin));
        linePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(myResources.getColor(R.color.notepad_lines));

       paperColor= myResources.getColor(R.color.notepad_paper);
       margin = myResources.getDimension(R.dimen.notepad_margin);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(paperColor);

        canvas.drawLine(0,0,0,getMeasuredHeight(),marginPaint);
        canvas.drawLine(0,getMeasuredHeight(),getMeasuredWidth(),getMeasuredHeight(),linePaint);

        canvas.drawLine(margin,0,margin,getMeasuredHeight(),marginPaint);

        canvas.save();
        canvas.translate(margin,0);
        super.onDraw(canvas);
        canvas.restore();;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
