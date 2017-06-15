package gclue.com.camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

class MyView extends View {

    private int x;
    private int y;

    /**
     * コンストラクタ
     *
     * @param context
     */
    public MyView(Context context) {
        super(context);
        setFocusable(true);
    }

    /**
     * 描画処理
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 描画するための線の色を設定.
        Paint mPaint = new Paint();
        mPaint.setStyle( Paint.Style.FILL );
        mPaint.setARGB( 255, 255, 255, 100 );

        // 長方形を描画.
        canvas.drawRect( x, y, x + 100, y + 100, mPaint );
    }

    /**
     * タッチイベント
     */
    public boolean onTouchEvent(MotionEvent event) {

        // X,Y座標の取得
        x = (int)event.getX();
        y = (int)event.getY();

        // 再描画の指示
        invalidate();

        return true;
    }
}