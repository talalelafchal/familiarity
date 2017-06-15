

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import ********.TouchableWrapperListener;

/**
 * @author Pablo Johnson (pablo.88j@gmail.com)
 */
public class TouchableWrapper extends FrameLayout {

    private static final long ANIMATION_DELAY = 500;
    private TouchableWrapperListener mListener;
    private boolean touched;
    private boolean touchStarted;

    public TouchableWrapper(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(TouchableWrapperListener listener) {
        mListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!touched) {
                    touched = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (touched && !touchStarted) {
                                touchStarted = true;
                                mListener.onTouchStart();
                            }

                        }
                    }, ANIMATION_DELAY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touched) {
                    touched = false;
                    if (touchStarted) {
                        touchStarted = false;
                        mListener.onTouchEnd();
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

}
