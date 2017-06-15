import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

/**
 * A button whose scale increases with a bouncy effect when enabled, and shrinks when disabled.
 *
 * Created by matt on 15/08/16.
 */
public class BouncyButton extends Button {

    private static final double TENSION = 40.0d;
    private static final double FRICTION = 3.0d;
    private static final double START_SCALE = 0.8d;
    private static final double END_SCALE = 1.0d;

    private final BounceSpringListener springListener = new BounceSpringListener();
    private Spring scaleSpring;

    public BouncyButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        BaseSpringSystem springSystem = SpringSystem.create();
        scaleSpring = springSystem.createSpring();
        scaleSpring.setSpringConfig(new SpringConfig(TENSION, FRICTION));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        scaleSpring.addListener(springListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        scaleSpring.removeListener(springListener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            scaleSpring.setEndValue(1);
        } else {
            scaleSpring.setEndValue(0);
        }
    }

    private class BounceSpringListener extends SimpleSpringListener {
        @Override
        public void onSpringUpdate(Spring spring) {
            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(
                    spring.getCurrentValue(), 0, 1, START_SCALE, END_SCALE);
            setScaleX(mappedValue);
            setScaleY(mappedValue);
        }
    }

}
