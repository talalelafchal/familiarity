public abstract class M360Activity extends AppCompatActivity implements ErrorDisplayerInterface {

    private static final String BUNDLE_ERROR_DISPLAYER_CALLBACK_LIST = "BUNDLE_ERROR_DISPLAYER_CALLBACK_LIST";


    private MixpanelAPI mixpanel;
    protected Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        // Initialize the library with your
        // Mixpanel project token, MIXPANEL_TOKEN, and a reference
        // to your application context.
        mixpanel =
                MixpanelAPI.getInstance(this, AppConstants.MIXPANEL_TOKEN);

        if (savedInstanceState != null) {
            ArrayList<ErrorDisplayerCallback> temp = (ArrayList<ErrorDisplayerCallback>) savedInstanceState.getSerializable("BUNDLE_ERROR_DISPLAYER_CALLBACK_LIST");
            if (temp != null) {
                callbackList = temp;
            }
        }
    }

   

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (presenter != null) {
            presenter.onSaveInstanceState(outState);
        }

    }

    @Override
    protected void onDestroy() {
        // When you call flush, the library attempts to send all of it's remaining messages.
        // If you don't call flush, the messages will be sent the next time the application is opened.
        mixpanel.flush();
        super.onDestroy();
        if (realm != null) { // guard against weird low-budget phones
            realm.close();
            realm = null;
        }
    }



    //-- PRESENTATION -

    protected ActivityPresenter presenter;

    public ActivityPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(ActivityPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.onStart();
        }
    }


    @Override
    public void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        if (presenter != null) {
            presenter.onRestoreInstanceState(savedState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.onResume();
        }
    }


}