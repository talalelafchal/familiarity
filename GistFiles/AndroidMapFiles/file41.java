public class TypeFaceEditText extends EditText {
  static Map<String, Typeface> inflatedFonts = new HashMap<>();

  public TypeFaceEditText(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs);
  }

  public TypeFaceEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public TypeFaceEditText(Context context) {
    super(context);
    init(null);
  }

  private void init(AttributeSet attrs) {
    if (attrs != null) {
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TypeFaceEditText);
      String fontName = a.getString(R.styleable.TypeFaceEditText_etFontName);
      if (fontName != null) {
        Typeface myTypeface = inflatedFonts.get(fontName);
        if (myTypeface == null) {
          myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
          inflatedFonts.put(fontName, myTypeface);
        }
        setTypeface(myTypeface);
      }
      a.recycle();
    }

    handleActionBtnClick();
  }

  private void handleActionBtnClick() {
    setOnEditorActionListener(new OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        ((InputMethodManager) v.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
            v.getWindowToken(), 0);
        clearFocus();
        return false;
      }
    });
  }

  @Override public boolean onKeyPreIme(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      clearFocus();
    }
    return super.onKeyPreIme(keyCode, event);
  }
}
