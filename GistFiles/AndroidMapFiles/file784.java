package com.comalia.gesicamobile.manager.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class TypefaceUtil {

    private static HashMap<String, Typeface> typeFaces = new HashMap<String, Typeface>();

    public static Typeface getTypeFace(Context context, String font) {
        Typeface tf = typeFaces.get(font);
        if (tf == null) {
            tf = Typeface.createFromAsset(context.getAssets(), font);
            typeFaces.put(font, tf);
        }
        return tf;
    }
}
