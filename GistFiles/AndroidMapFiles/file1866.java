public class RxValidationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_validation);

        TextView mInputField = (TextView) findViewById(R.id.input);
        TextInputLayout mInputLayout = (TextInputLayout) findViewById(R.id.input_layout);

        TextView mPasswordInputField = (TextView) findViewById(R.id.password);
        TextInputLayout mPasswordInputLayout = (TextInputLayout) findViewById(R.id.password_layout);

        ViewObservable.text(mInputField)
                .compose(mapString())
                .compose(validateInput())
                .compose(debounceOnMain(1))
                .subscribe(valid -> {
                    mInputLayout.setErrorEnabled(!valid);
                    if (!valid) {
                        mInputLayout.setError("Invalid email");
                    }
                });

        ViewObservable.text(mPasswordInputField)
                .compose(mapString())
                .compose(validatePassword())
                .compose(debounceOnMain(1))
                .subscribe(valid -> {
                    mPasswordInputLayout.setErrorEnabled(!valid);
                    if (!valid) {
                        mPasswordInputLayout.setError("Invalid Password");
                    }
                });

    }

    public static Observable.Transformer<TextView, String> mapString() {
        return observable -> observable.map(textView -> textView.getText().toString());
    }

    public static Observable.Transformer<String, Boolean> validateInput() {
        return observable -> observable.map(text -> text.matches(Patterns.EMAIL_ADDRESS.toString()));
    }

    public static Observable.Transformer<String, Boolean> validatePassword() {
        return observable -> observable.map(text -> text.matches("password"));
    }

    public static Observable.Transformer<Boolean, Boolean> debounceOnMain(long aSeconds) {
        return aIn1 -> aIn1.debounce(aSeconds, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread());
    }
    
        <android.support.design.widget.TextInputLayout
        android:layout_width="match_parent"
        android:id="@+id/input_layout"
        android:layout_height="wrap_content">

        <EditText
            android:id="@+id/input"
            android:hint="Email"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>

    </android.support.design.widget.TextInputLayout>

    <android.support.design.widget.TextInputLayout
        android:layout_below="@id/input_layout"
        android:layout_width="match_parent"
        android:id="@+id/password_layout"
        android:layout_height="wrap_content">

        <EditText
            android:id='@+id/password'
            android:layout_marginTop="20dp"
            android:hint="Password"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>

    </android.support.design.widget.TextInputLayout>