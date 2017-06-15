public final class TypefaceUtil {
    private static final String TAG = TypefaceUtil.class.getSimpleName();

    private TypefaceUtil() {
        //Only air
    }

    /**
     * Using reflection to override default typeface
     * NOTICE: DO NOT FORGET TO SET TYPEFACE FOR APP THEME AS DEFAULT TYPEFACE WHICH WILL BE OVERRIDDEN
     *
     * @param context                    to work with assets
     * @param defaultFontNameToOverride  for example "monospace"
     * @param customFontFileNameInAssets file name of the font from assets
     */
    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);
            // Check if we're running on Android 5.0 or higher
            if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
                Map<String, Typeface> newMap = new HashMap();
                newMap.put("sans-serif", customFontTypeface);
                try {
                    final Field staticField = Typeface.class.getDeclaredField("sSystemFontMap");
                    staticField.setAccessible(true);
                    staticField.set(null, newMap);
                } catch (NoSuchFieldException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            } else {
                try {
                    final Field staticField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
                    staticField.setAccessible(true);
                    staticField.set(null, customFontTypeface);
                } catch (NoSuchFieldException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Log.e(TAG, "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
        }
    }
}