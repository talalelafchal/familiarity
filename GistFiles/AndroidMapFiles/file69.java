package my;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.example.client.R;

/**
 * Description:
 * <p/>
 * Date: 14-2-3
 * Author: Administrator
 */
public class EditText extends android.widget.EditText implements Position, ModelBinder {
    private String p;
    private String field;

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TableLayout);
        p = typedArray.getString(R.styleable.TableLayout_p);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ModelBinder);
        field = typedArray.getString(R.styleable.ModelBinder_field);
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public void setValue(Object value) {
        this.setText((String) value);
    }

    @Override
    public Object getValue() {
        return getText().toString();
    }

    @Override
    public String getP() {
        return p;
    }
}
