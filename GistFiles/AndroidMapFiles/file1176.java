public class CustomTextView extends TextView {
    public static final String TAG = "CustomTextView";

    public static final String FONT_MYFONT_BLACK = "myFontBlack";

    public static final String FONT_MYFONT_BOLD = "myFontBold";

    public static final String FONT_MYFONT_ITALIC = "myFontItalic";

    public static final String FONT_MYFONT_REGULAR = "myFontRegular";

    public static final String FONT_PATH_MYFONT_BLACK = "fonts/MyFont-Black.otf";

    public static final String FONT_PATH_MYFONT_BOLD = "fonts/MyFont-Bold.otf";

    public static final String FONT_PATH_MYFONT_ITALIC = "fonts/MyFont-Italic.otf";

    public static final String FONT_PATH_MYFONT_REGULAR = "fonts/MyFont-Regular.otf";

    public static final int FONT_ID_INVALID = -1;

    public static final int FONT_ID_MYFONT_BLACK = 1;

    public static final int FONT_ID_MYFONT_BOLD = 2;

    public static final int FONT_ID_MYFONT_ITALIC = 3;

    public static final int FONT_ID_MYFONT_REGULAR = 4;

    private Typeface mTypeface;

    private int mFont = FONT_ID_INVALID;

    public CustomTextView(final Context context) {
        this(context, null);
    }

    public CustomTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String customFont = a.getString(R.styleable.CustomTextView_customFont);
        setCustomFont(context, getFont(customFont));
        a.recycle();
    }

    private int getFont(String s) {
        if (FONT_MYFONT_BLACK.equals(s)) {
            return FONT_ID_MYFONT_BLACK;
        } else if (FONT_MYFONT_BOLD.equals(s)) {
            return FONT_ID_MYFONT_BOLD;
        } else if (FONT_MYFONT_ITALIC.equals(s)) {
            return FONT_ID_MYFONT_ITALIC;
        } else if (FONT_MYFONT_REGULAR.equals(s)) {
            return FONT_ID_MYFONT_REGULAR;
        }
        return FONT_ID_INVALID;
    }

    public void setCustomFont(Context context, int font) {
        if (mFont == font && mTypeface != null) {
            return;
        }
        switch (font) {
            case FONT_ID_MYFONT_BLACK:
                mTypeface = ViewUtils.getTypeface(context, FONT_PATH_MYFONT_BLACK);
                break;
            case FONT_ID_MYFONT_BOLD:
                mTypeface = ViewUtils.getTypeface(context, FONT_PATH_MYFONT_BOLD);
                break;
            case FONT_ID_MYFONT_ITALIC:
                mTypeface = ViewUtils.getTypeface(context, FONT_PATH_MYFONT_ITALIC);
                break;
            case FONT_ID_MYFONT_REGULAR:
                mTypeface = ViewUtils.getTypeface(context, FONT_PATH_MYFONT_REGULAR);
                break;
            default:
                mTypeface = ViewUtils.getTypeface(context, FONT_PATH_MYFONT_REGULAR);
        }
        if (mTypeface != null) {
            setTypeface(mTypeface);
        }
    }
}