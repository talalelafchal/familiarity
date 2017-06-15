package org.example.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Created by y-kawakami on 13/11/13.
 */
public class MyView extends View {

    public MyView(Context context){
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(Color.WHITE);
        paint.setTextSize(64);
        canvas.drawText("TEST-TEST-TEST", 100,100, paint);
    }

/**
 *  とりあえず用意しておいたが、これはまだ使っていない
 */
    public void draw(){
        invalidate();
    }
}
