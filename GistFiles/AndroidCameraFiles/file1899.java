import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AwesomeTextView extends TextView {
    public AwesomeTextView(Context context) {
        super(context);
        setFont();
    }

    public AwesomeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public AwesomeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    void setFont() {
        Typeface awesomeTypeface = Typeface.createFromAsset(
                getContext().getAssets(), "fonts/fontawesome-webfont.ttf");
        this.setTypeface(awesomeTypeface);
    }
}
  