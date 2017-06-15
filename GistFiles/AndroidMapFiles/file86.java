import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontUtils {

    private CustomFontUtils() {
        // No instances
    }

    public static void parseAttributes(TextView textView, Context context, AttributeSet attrs, int defStyle) {
        if(textView.isInEditMode())
            return; // Typeface loading isn't implemented in the IDE

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFont);
        try {
            String font = a.getString(R.styleable.CustomFont_font);
            if(font != null)
                textView.setTypeface(CustomFontManager.getTypeface(context.getAssets(), "fonts/" + font));
        } finally {
            a.recycle();
        }
    }

}
