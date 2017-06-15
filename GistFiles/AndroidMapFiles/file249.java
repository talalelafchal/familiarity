import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class CustomFontManager {

    private static Map<String, Typeface> typefaces = new HashMap<String, Typeface>();

    private CustomFontManager() {
        // No instances
    }

    public static Typeface getTypeface(AssetManager assetManager, String name) {
        if(typefaces.containsKey(name))
            return typefaces.get(name);
        else
            return loadTypeface(assetManager, name);
    }

    private static synchronized Typeface loadTypeface(AssetManager assetManager, String name) {
        if(typefaces.containsKey(name))
            return typefaces.get(name);

        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(assetManager, name);
        } catch(RuntimeException e) {
            throw new IllegalArgumentException("Unable to load typeface. Did you remember to add it to assets/fonts?", e);
        }

        typefaces.put(name, typeface);
        return typeface;
    }

}
