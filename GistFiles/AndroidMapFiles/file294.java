package com.test.customWidgets;



import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class CustomTextView extends TextView{
	Utility utility = new Utility();

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//Typeface.createFromAsset doesn't work in the layout editor. Skipping...
		if (isInEditMode()) {
			return;
		}

		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
		String fontNos = styledAttrs.getString(R.styleable.TypefacedTextView_typeface);
		styledAttrs.recycle();
		String font = utility.getFontName(Integer.valueOf(fontNos));
		if(font != null) {
			Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/"+font);
			setTypeface(typeface);
		}
	}
}
