public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";

    private Typeface fontBold, fontRegular, fontItalic;
    private CustomTextView mText;
    private ArrayList<MyBean> mBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        if (savedInstanceState == null) {
            mBeans = bundle.getParcelableArrayList("beans");
        } else {
            mBeans = savedInstanceState.getParcelableArrayList("beans");
        }

        fontBold = ViewUtils.getTypeface(getBaseContext(), CustomTextView.FONT_PATH_MYFONT_BOLD);
        fontRegular = ViewUtils.getTypeface(getBaseContext(), CustomTextView.FONT_PATH_MYFONT_REGULAR);
        fontItalic = ViewUtils.getTypeface(getBaseContext(), CustomTextView.FONT_PATH_MYFONT_ITALIC);

        mText = (CustomTextView) findViewById(R.id.text);

        setText(mText, mBeans);
    }

    private void setText(TextView textView, ArrayList<MyBean> text) {
        for (int i = 0; i < text.size(); i++) {
            if (i == 0) {
                textView.setText("");
            }
            if (text.get(i).bold != null && text.get(i).bold.length() > 0) {
                SpannableString spannable = new SpannableString(text.get(i).bold);
                spannable.setSpan(new CustomTypefaceSpan("", fontBold), 0, text.get(i).bold.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                textView.append(spannable);
            }
            if (text.get(i).italic != null && text.get(i).italic.length() > 0) {
                SpannableString spannable = new SpannableString(text.get(i).italic);
                spannable.setSpan(new CustomTypefaceSpan("", fontItalic), 0, text.get(i).italic.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                textView.append(spannable);
            }
            if (text.get(i).regular != null && text.get(i).regular.length() > 0) {
                SpannableString spannable = new SpannableString(text.get(i).regular);
                spannable.setSpan(new CustomTypefaceSpan("", fontRegular), 0, text.get(i).regular.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                textView.append(spannable);
            }
            if (!(text.get(i).bold == null && text.get(i).italic == null && text.get(i).regular == null)
                    && i != text.size() - 1) {
                textView.append("\n\n");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("beans", mBeans);
        super.onSaveInstanceState(outState);
    }
}