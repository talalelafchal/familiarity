public class FontsReplacer {

    @SuppressWarnings("unchecked")
    public static void replaceFonts(Context context) {
        try {
            //we need to change all static fields contains fonts:

            //1. static constants
            setStaticFinalField(Typeface.class, "DEFAULT", FontCache.getTypeface(context, Typeface.NORMAL));
            setStaticFinalField(Typeface.class, "DEFAULT_BOLD", FontCache.getTypeface(context, Typeface.BOLD));
            setStaticFinalField(Typeface.class, "SANS_SERIF", FontCache.getTypeface(context, Typeface.NORMAL));
            setStaticFinalField(Typeface.class, "SERIF", FontCache.getTypeface(context, Typeface.NORMAL));
            setStaticFinalField(Typeface.class, "MONOSPACE", FontCache.getTypeface(context, Typeface.NORMAL));

            //2. array with defaults
            setStaticFinalField(Typeface.class, "sDefaults", new Typeface[] {
                    Typeface.DEFAULT,
                    Typeface.DEFAULT_BOLD,
                    FontCache.getTypeface(context, Typeface.ITALIC),
                    FontCache.getTypeface(context, Typeface.BOLD_ITALIC)
            });

            //3. invoke method 'setDefault'
            Method setDefault = Typeface.class.getDeclaredMethod("setDefault", Typeface.class);
            setDefault.setAccessible(true);
            setDefault.invoke(null, Typeface.DEFAULT);

            //4. replace fonts in the map
            Field sSystemFontMapField = Typeface.class.getDeclaredField("sSystemFontMap");
            sSystemFontMapField.setAccessible(true);
            Map<String, Typeface> sSystemFontMap = (Map<String, Typeface>) sSystemFontMapField.get(null);
            for (String key : sSystemFontMap.keySet()) {
                sSystemFontMap.put(key, FontCache.getDefaultTypeface(context));
            }

        } catch (Exception e) {
            Timber.e(e, e.getMessage());
        }
    }

    private static void setStaticFinalField(Class clazz, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        try {
            Field modifiers = Field.class.getDeclaredField("accessFlags");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
        }

        field.set(null, value);
    }

    private FontsReplacer() {}
}