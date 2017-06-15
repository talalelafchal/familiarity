package test.fragment.list;

import test.fragment.list.ImageFactory.Type;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CustomImageView extends ImageView {

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        if (bitmap == null || bitmap.isRecycled()) {
            setImageBitmap(ImageFactory.getBitmap(getContext(), Type.LOADING));
        }
        super.onDraw(canvas);
    }
}
