import android.app.Activity;

public class LayerView extends View {
    private Paint mPaint;
    private int mViewWidth,mViewHeight;
    public LayerView(Context context, AttributeSet attrs)
    {
        super(context,attrs);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
    }

}