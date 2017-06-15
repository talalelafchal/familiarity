/**
 * A thread-safe class that loads and caches typefaces.
 */
public final class TypefaceCache
{
    private static final int INITIAL_CAPACITY = 2;

    private static final ConcurrentHashMap<String, Typeface> pathToTypeface
        = new ConcurrentHashMap<>(INITIAL_CAPACITY);

    private TypefaceCache()
    {
        // Hiding constructor
    }

    /**
     * Returns a cached typeface or creates a new one if it is not present.
     *
     * @param context Context whose assets are used.
     * @param path    The file name of the font data in the assets directory.
     */
    public static Typeface getTypeface(Context context, String path)
    {
        // See Effective Java item 69
        Typeface typeface = pathToTypeface.get(path);
        if (typeface == null)
        {
            Typeface newTypeface = Typeface.createFromAsset(context.getAssets(), path);
            typeface = pathToTypeface.putIfAbsent(path, newTypeface);
            if (typeface == null)
            {
                typeface = newTypeface;
            }
        }

        return typeface;
    }
}
