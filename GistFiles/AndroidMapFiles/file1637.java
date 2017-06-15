package my;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.example.client.R;

/**
 * Description:
 * <p/>
 * Date: 14-2-3
 * Author: Administrator
 */
public class Button extends android.widget.Button implements Position, Event {
    private String p;

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TableLayout);
        p = typedArray.getString(R.styleable.TableLayout_p);
    }

    @Override
    public String getP() {
        return p;
    }

    @Override
    public void addEventHandler(final EventHandler eventHandler) {
        if (eventHandler != null) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventHandler.fire(Button.this.getId(), Button.this);
                }
            });
        }
    }
}
