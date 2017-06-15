public class ViewUtils {
    public static final String TAG = "ViewUtils";
    
    private static HashMap<String, Typeface> fonts = new HashMap<>();

    public static Typeface getTypeface(Context context, String fontPath) {
        Typeface typeface = fonts.get(fontPath);
        if (typeface != null) {
            return typeface;
        } else {
            typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
            fonts.put(fontPath, typeface);
            return typeface;
        }
    }
}