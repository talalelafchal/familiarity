
public class DLTypefaceHelper {

    private static final String TAG="DLTypefaceHelper";

    //using map to cache the typeface for avoiding memory leak
    public static final HashMap<String, Typeface> fontMap = new HashMap<String, Typeface>();

    public static void applyFont(Context context, String font, TextView... textViews){

        if(context == null || textViews == null || font == null) {
            return;
        }

        Typeface typeface = null;
        if(fontMap.containsKey(font)){
            typeface = fontMap.get(font);
        }else{
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + font);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "Could not create font "+font+" from assets/fonts folder");
            }
            if(typeface == null){
                Log.e(TAG, "Could not find font "+font+" from assets/fonts folder");
                return;
            }
            fontMap.put(font, typeface);
        }

        for (TextView textView : textViews) {
            if(textView != null) {
                textView.setTypeface(typeface);
            }
        }
    }
}
