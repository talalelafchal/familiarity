package com.ics.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marius on 30/7/15.
 *
 * A one-time lazy loader class for custom text fonts
 *
 * @music Epic 45 - We were never here
 */
public class FontLoader {

    private static Map<String, Typeface> typefaces = new HashMap<>();

    public static Typeface getTypeface(Context context, String font) {
        if (!typefaces.containsKey(font)) {
            final Typeface typeface = Typeface.createFromAsset(context.getAssets(), font);
            typefaces.put(font, typeface);
            return typeface;
        }
        return typefaces.get(font);
    }
}
