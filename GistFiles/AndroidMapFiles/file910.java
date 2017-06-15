/*
 * Copyright (c) 2015 Tom Wijgers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Except as contained in this notice, the name(s) of the above copyright holders shall not be used
 * in advertising or otherwise to promote the sale, use or other dealings in this Software without
 * prior written authorization.
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.sss.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Wijgers
 */
public class TypefaceManager
{
    private static final String TAG = TypefaceManager.class.getName();

    private static final Map<String, Typeface> sTypefaces = new HashMap<>();
    private static Context sContext = null;

    public static void init(Context c)
    {
        if(sContext == null)
            sContext = c;
    }

    public static Typeface getTypeface(String font)
    {
        Typeface tf = null;

        if(sTypefaces.containsKey(font))
            tf = sTypefaces.get(font);
        else if (sContext != null)
        {

            try
            {
                tf = Typeface.createFromAsset(sContext.getAssets(), font);
            }
            catch(RuntimeException rte)
            {
                Log.e(TAG, rte.getMessage(), rte);
            }


            if(tf != null)
                sTypefaces.put(font, tf);
        }

        return tf;
    }
}