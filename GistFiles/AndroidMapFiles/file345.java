/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package your package;
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
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import your R file;

/**
 * Created by norman on 3/8/15.
 */
public class CustomFontUtils {

    public static void applyCustomFont(TextView customFontTextView, Context context, AttributeSet attrs) {
        final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.ChatFontTextView);

        String fontName = attributeArray.getString(R.styleable.ChatFontTextView_font);

        // check if a special textStyle was used (e.g. extra bold)
        int textStyle = attributeArray.getInt(R.styleable.ChatFontTextView_textStyle, 0);

        // if nothing extra was used, fall back to regular android:textStyle parameter
        if (textStyle == 0) {
            textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        }

        Typeface customFont = selectTypeface(context, fontName, textStyle);
        customFontTextView.setTypeface(customFont);

        attributeArray.recycle();
    }

    private static Typeface selectTypeface(Context context, String fontName, int textStyle) {
        if (fontName.contentEquals(context.getString(R.string.font_name_fontawesome))) {
            return FontCache.getTypeface("fonts/fontawesome.ttf", context);
        }
        else if (fontName.contentEquals(context.getString(R.string.font_name_source_sans_pro))) {
            /*
            information about the TextView textStyle:
            http://developer.android.com/reference/android/R.styleable.html#TextView_textStyle
            */
            switch (textStyle) {
                case Typeface.BOLD: // bold
                    return FontCache.getTypeface("fonts/SourceSansPro-Bold.ttf", context);

                case Typeface.ITALIC: // italic
                    return FontCache.getTypeface("fonts/SourceSansPro-Italic.ttf", context);

                case Typeface.BOLD_ITALIC: // bold italic
                    return FontCache.getTypeface("fonts/SourceSansPro-BoldItalic.ttf", context);

                case 10: // extra light, equals @integer/font_style_extra_light
                    return FontCache.getTypeface("fonts/SourceSansPro-ExtraLight.ttf", context);

                case 11: // extra bold, equals @integer/font_style_extra_bold
                    return FontCache.getTypeface("fonts/SourceSansPro-Black.ttf", context);

                case Typeface.NORMAL: // regular
                default:
                    return FontCache.getTypeface("fonts/SourceSansPro-Regular.ttf", context);
            }
        }
        else {
            // no matching font found
            // return null so Android just uses the standard font (Roboto)
            return null;
        }
    }
}