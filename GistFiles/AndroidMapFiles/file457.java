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

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewTreeObserver;
import android.view.Window;

/**
 * @author Tom Wijgers
 */
public class AppCompatUtils
{
    @SuppressWarnings("unused")
    private static final String TAG = AppCompatUtils.class.getName();

    public static @ColorInt int getColor(@NonNull Resources res, @ColorRes int color)
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            //noinspection deprecation
            return res.getColor(color);
        }
        else
        {
            return res.getColor(color, null);
        }
    }

    public static @ColorInt int getColor(@NonNull Resources res, @ColorRes int color, @Nullable Resources.Theme theme)
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            //noinspection deprecation
            return res.getColor(color);
        }
        else
        {
            return res.getColor(color, theme);
        }
    }

    public static Drawable getDrawable(@NonNull Resources res, @DrawableRes int drawable)
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            //noinspection deprecation
            return res.getDrawable(drawable);
        }
        else
        {
            return res.getDrawable(drawable, null);
        }
    }

    public static Drawable getDrawable(@NonNull Resources res, @DrawableRes int drawable, @Nullable Resources.Theme theme)
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            //noinspection deprecation
            return res.getDrawable(drawable);
        }
        else
        {
            return res.getDrawable(drawable, theme);
        }
    }

    public static void removeViewTreeObserver(@NonNull ViewTreeObserver obs, @NonNull ViewTreeObserver.OnGlobalLayoutListener listener)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            obs.removeOnGlobalLayoutListener(listener);
        }
        else
        {
            //noinspection deprecation
            obs.removeGlobalOnLayoutListener(listener);
        }
    }

    public static void setStatusBarColor(@NonNull Window window, @ColorInt int color)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            window.setStatusBarColor(color);
        }
    }
}