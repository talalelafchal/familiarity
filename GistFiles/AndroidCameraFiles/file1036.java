package com.example;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AxisjTextView extends TextView {
    public AxisjTextView(Context context) {
        super(context);
        setFont();
    }

    public AxisjTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public AxisjTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    void setFont() {
        Typeface awesomeTypeface = Typeface.createFromAsset(
                getContext().getAssets(), "fonts/axicon.ttf");
        this.setTypeface(awesomeTypeface);
    }
}