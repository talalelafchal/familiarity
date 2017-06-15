package com.yourpackage.util;
/**
 The MIT License (MIT)

 Copyright (c) 2015 Future Studio

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 04-11-2016.
 */
public class ChatFontTextView extends TextView {

    public ChatFontTextView(Context context) {
        super(context);
        CustomFontUtils.applyCustomFont(this, context, null);
      //  applyCustomFont(context);
    }

    public ChatFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

       CustomFontUtils.applyCustomFont(this, context, attrs);

      //  applyCustomFont(context);
    }

    public ChatFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontUtils.applyCustomFont(this, context, attrs);
       // applyCustomFont(context);
    }

 /*   private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/SourceSansPro-Regular.ttf", context);
        setTypeface(customFont);
    }*/


}