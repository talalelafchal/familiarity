
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a font wrapper for android. Currently there is no custom font system supported by android.
 * So if you dont want to use a third party font injecter, you can use this class.
 * Usage: 
 * 1- Add this class to your project
 * 2- Override it and name MyFonts.java and fill fonts in initialize() with add(...) method
 * 3- In your Application.java onCreate method call once Font.initialize(this, MyFonts.class)
 * 
 * To place fonts into views use one of the static set methods wherever you need
 * examples: 
 *
 * Font.set(MyFonts.HelveticaRG, textView); //sets font for a textview
 * Font.set(MyFonts.HelveticaRG, MainActivity.this, R.id.text1, R.id.text2, ...); //sets font for text ids in a viewgroup
 * Font.set(MyFonts.HelveticaRG, MainActivity.this) //sets font for all textviews in viewgroup
 * and my favorite...
 * Font.setByTag(MyFonts.HelveticaBLD, this) //set android:tag="helvetica-bold" of textview in layout xml file and this will assign the font to the textview
 * 
 */
public abstract class Font {
    static Font instance;
    AssetManager mgr;
    static Map<String,Typeface> Fonts = new HashMap<>();

    public static void set(String fontName,TextView... views){
        Typeface font = Fonts.get(fontName);
        for (TextView v :
                views) {
            v.setTypeface(font);
        }
    }

    public static Typeface get(String fontName){
        return Fonts.get(fontName);
    }
    public static void set(String fontName, ViewGroup grp, int... views){
        Typeface font = Fonts.get(fontName);
        for (int i :
                views) {
            TextView v = (TextView) grp.findViewById(i);
            v.setTypeface(font);
        }
    }
    public static void set(String fontName, ViewGroup grp){
        for (int i = 0; i < grp.getChildCount(); i++) {
            View child = grp.getChildAt(i);

            if (child instanceof ViewGroup) {
                set(fontName, (ViewGroup) child);
            } else if (child instanceof TextView) {
                set(fontName, (TextView) child);
            }
        }
    }
    public static void set(String fontName, Activity act,@IdRes int... views){
        Typeface font = Fonts.get(fontName);
        for (int i :
                views) {
            TextView v = (TextView) act.findViewById(i);
            v.setTypeface(font);
        }
    }

    public static void setByTag(ViewGroup layout){
        for (int i=0;i < layout.getChildCount();i++){
            View v = layout.getChildAt(i);
            if (v instanceof ViewGroup)
                setByTag((ViewGroup)v);
            else if (v instanceof TextView){
                TextView tx = (TextView)v;
                String tag = (String)tx.getTag();
                if (Fonts.containsKey(tag))
                    set(tag,tx);
            }
        }
    }

    public static void initialize(Context context, Class<? extends Font> cls){
        AssetManager assets = context.getAssets();
        try {
            Font.instance = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        instance.mgr = assets;
        instance.initialize();
    }

    public void add(String fontName, String assetPath){
        Fonts.put(fontName,Typeface.createFromAsset(instance.mgr, assetPath));
    }
    public void add(String assetPath){
        add(assetPath,assetPath);
    }
    protected abstract void initialize();
}
