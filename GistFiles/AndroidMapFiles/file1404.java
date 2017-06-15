package com.comalia.gesicamobile.manager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.comalia.gesicamobile.manager.R;
import com.comalia.gesicamobile.manager.util.TypefaceUtil;

public class FontableTextView extends TextView {

    public FontableTextView(Context context) {
        super(context);
    }

    public FontableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FontableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FontableTextView);
        String customFont = a.getString(R.styleable.FontableTextView_customFont);
        if (!TextUtils.isEmpty(customFont)) {
            setCustomFont(customFont);
        }
        a.recycle();
    }

    public void setCustomFont(String customFont) {
        try {
            setTypeface(TypefaceUtil.getTypeFace(getContext(), customFont));
        } catch (Exception e) {
            Log.e("FontableTextView", "Could not get typeface: " + e.getMessage());
        }
    }
}
